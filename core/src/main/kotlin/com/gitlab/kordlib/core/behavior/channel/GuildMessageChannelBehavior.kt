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
        val request = BulkDeleteRequest(messages.map { it.toString() })
        kord.rest.channel.bulkDelete(id.toString(), request)
    }

    suspend fun getPinnedMessage(): Flow<Nothing /*Message*/> = TODO()

    suspend fun createWebhook(builder: NewWebhookBuilder): Nothing /*Webhook*/ = TODO()

    companion object {
        internal operator fun <T : UpdateGuildChannelBuilder> invoke(id: Snowflake, kord: Kord) = object : GuildMessageChannelBehavior<T> {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }
}

suspend inline fun <T : UpdateGuildChannelBuilder> GuildMessageChannelBehavior<T>.createWebhook(builder: NewWebhookBuilder.() -> Unit): Nothing =
        createWebhook(NewWebhookBuilder().also(builder))