package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.webhook.NewWebhookBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.BulkDeleteRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
interface GuildMessageChannelBehavior : CategorizableChannelBehavior, MessageChannelBehavior {

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

    /**
     * Requests to get the pinned messages in this channel.
     */
    suspend fun getPinnedMessage(): Flow<Nothing /*Message*/> = TODO()

    companion object {
        internal operator fun invoke(guildId: Snowflake, categoryId: Snowflake, id: Snowflake, kord: Kord) = object : GuildMessageChannelBehavior {
            override val guildId: Snowflake = guildId
            override val categoryId: Snowflake = categoryId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }
}

suspend inline fun GuildMessageChannelBehavior.createWebhook(builder: NewWebhookBuilder.() -> Unit): Nothing = TODO()
