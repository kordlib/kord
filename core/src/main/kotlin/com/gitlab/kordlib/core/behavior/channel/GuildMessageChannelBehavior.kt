package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.builder.webhook.WebhookCreateBuilder
import com.gitlab.kordlib.core.cache.data.WebhookData
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.Webhook
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.rest.json.request.BulkDeleteRequest

/**
 * The behavior of a Discord message channel associated to a [guild].
 */
interface GuildMessageChannelBehavior : GuildChannelBehavior, MessageChannelBehavior {

    override suspend fun asChannel(): GuildMessageChannel {
        return super<GuildChannelBehavior>.asChannel() as GuildMessageChannel
    }

    /**
     * Requests to bulk delete the [messages].
     *
     * @throws RequestException when trying to delete messages older than 14 days.
     */
    //TODO 1.3.50 return messages that are older than 14 days
    suspend fun bulkDelete(messages: Iterable<Snowflake>) {
        messages.asSequence()
                .map { it.value }
                .chunked(100)
                .map { BulkDeleteRequest(it) }
                .forEach { kord.rest.channel.bulkDelete(id.value, it) }
    }

    //TODO 1.3.50 add delete messages? partially bulkdelete, manually delete older ones

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord) = object : GuildMessageChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
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
    val builder = WebhookCreateBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.webhook.createWebhook(id.value, request, reason)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}
