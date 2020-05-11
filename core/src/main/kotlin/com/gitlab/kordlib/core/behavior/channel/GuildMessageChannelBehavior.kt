package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.rest.builder.webhook.WebhookCreateBuilder
import com.gitlab.kordlib.core.cache.data.WebhookData
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.Webhook
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.rest.json.request.BulkDeleteRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.time.days

/**
 * The behavior of a Discord message channel associated to a [guild].
 */
interface GuildMessageChannelBehavior : GuildChannelBehavior, MessageChannelBehavior {

    /**
     * Requests to get all webhooks for this channel.
     */

    val webhooks: Flow<Webhook>
        get() = flow {
            for(response in kord.rest.webhook.getChannelWebhooks(id.value)) {
                val data = WebhookData.from(response)
                emit(Webhook(data,kord))
            }

        }


    override suspend fun asChannel(): GuildMessageChannel {
        return super<GuildChannelBehavior>.asChannel() as GuildMessageChannel
    }

    /**
     * Requests to bulk delete the [messages]. Sequentially deletes messages older than 14 days.
     */
    suspend fun bulkDelete(messages: Iterable<Snowflake>) {
        //split up in bulk delete and manual delete
        // if message.timeMark + 14 days > now, then the message isn't 14 days old yet, and we can add it to the bulk delete
        // if message.timeMark + 14 days < now, then the message is more than 14 days old, and we'll have to manually delete them
        val messagesByRemoval = messages.groupBy { it.timeMark.plus(14.days).hasPassedNow() }
        val younger = messagesByRemoval[true].orEmpty()
        val older = messagesByRemoval[false].orEmpty()

        when {
            younger.size < 2 -> younger.forEach { kord.rest.channel.deleteMessage(id.value, it.value) }
            else -> younger.map { it.value }.chunked(100)
                    .map { BulkDeleteRequest(it) }
                    .forEach { kord.rest.channel.bulkDelete(id.value, it) }
        }

        older.forEach { kord.rest.channel.deleteMessage(id.value, it.value) }
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord) = object : GuildMessageChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord

            override fun hashCode(): Int = Objects.hash(id, guildId)

            override fun equals(other: Any?): Boolean = when(other) {
                is GuildChannelBehavior -> other.id == id && other.guildId == guildId
                is ChannelBehavior -> other.id == id
                else -> false
            }
        }
    }
}

/**
 * Requests to create a new webhook.
 *
 * @return The created [Webhook].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildMessageChannelBehavior.createWebhook(builder: WebhookCreateBuilder.() -> Unit): Webhook {
    val response = kord.rest.webhook.createWebhook(id.value, builder)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}
