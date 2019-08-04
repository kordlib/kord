package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateGuildChannelBuilder
import com.gitlab.kordlib.core.`object`.builder.webhook.NewWebhookBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.BulkDeleteRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
interface GuildMessageChannelBehavior<T : UpdateGuildChannelBuilder> : GuildChannelBehavior<T>, MessageChannelBehavior {

    suspend fun bulkDelete(messages: Iterable<Snowflake>) {
        val request = BulkDeleteRequest(messages.map { it.value })
        kord.rest.channel.bulkDelete(id.value, request)
    }

    suspend fun getPinnedMessage(): Flow<Nothing /*Message*/> = TODO()

    suspend fun createWebhook(builder: NewWebhookBuilder): Nothing /*Webhook*/ = TODO()

    companion object {
        internal operator fun <T : UpdateGuildChannelBuilder> invoke(guildId: Snowflake, id: Snowflake, kord: Kord) = object : GuildMessageChannelBehavior<T> {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }
}

suspend inline fun <T : UpdateGuildChannelBuilder> GuildMessageChannelBehavior<T>.createWebhook(builder: NewWebhookBuilder.() -> Unit): Nothing =
        createWebhook(NewWebhookBuilder().also(builder))