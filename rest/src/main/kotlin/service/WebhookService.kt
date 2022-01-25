package dev.kord.rest.service

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.DiscordMessage
import dev.kord.common.entity.DiscordWebhook
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.rest.builder.message.create.WebhookMessageCreateBuilder
import dev.kord.rest.builder.message.modify.WebhookMessageModifyBuilder
import dev.kord.rest.builder.webhook.WebhookCreateBuilder
import dev.kord.rest.builder.webhook.WebhookModifyBuilder
import dev.kord.rest.json.request.WebhookCreateRequest
import dev.kord.rest.json.request.WebhookEditMessageRequest
import dev.kord.rest.json.request.WebhookExecuteRequest
import dev.kord.rest.json.request.WebhookModifyRequest
import dev.kord.rest.request.RequestBuilder
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Route
import kotlinx.serialization.json.JsonObject
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class WebhookService(requestHandler: RequestHandler) : RestService(requestHandler) {

    suspend inline fun createWebhook(
        channelId: Snowflake,
        name: String,
        builder: WebhookCreateBuilder.() -> Unit
    ): DiscordWebhook {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.WebhookPost) {
            keys[Route.ChannelId] = channelId
            val createBuilder = WebhookCreateBuilder(name).apply(builder)
            body(WebhookCreateRequest.serializer(), createBuilder.toRequest())
            auditLogReason(createBuilder.reason)
        }
    }

    suspend fun getChannelWebhooks(channelId: Snowflake) = call(Route.ChannelWebhooksGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun getGuildWebhooks(guildId: Snowflake) = call(Route.GuildWebhooksGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getWebhook(webhookId: Snowflake) = call(Route.WebhookGet) {
        keys[Route.WebhookId] = webhookId
    }

    suspend fun getWebhookWithToken(webhookId: Snowflake, token: String) = call(Route.WebhookByTokenGet) {
        webhookIdToken(webhookId, token)
    }

    suspend inline fun modifyWebhook(webhookId: Snowflake, builder: WebhookModifyBuilder.() -> Unit): DiscordWebhook {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.WebhookPatch) {
            keys[Route.WebhookId] = webhookId
            val modifyBuilder = WebhookModifyBuilder().apply(builder)
            body(WebhookModifyRequest.serializer(), modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    suspend inline fun modifyWebhookWithToken(
        webhookId: Snowflake,
        token: String,
        builder: WebhookModifyBuilder.() -> Unit
    ): DiscordWebhook {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.WebhookByTokenPatch) {
            webhookIdToken(webhookId, token)
            val modifyBuilder = WebhookModifyBuilder().apply(builder)
            body(WebhookModifyRequest.serializer(), modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }
    }

    suspend fun deleteWebhook(webhookId: Snowflake, reason: String? = null) = call(Route.WebhookDelete) {
        keys[Route.WebhookId] = webhookId
        auditLogReason(reason)
    }

    suspend fun deleteWebhookWithToken(webhookId: Snowflake, token: String, reason: String? = null) =
        call(Route.WebhookByTokenDelete) {
            webhookIdToken(webhookId, token)
            auditLogReason(reason)
        }

    suspend inline fun executeWebhook(
        webhookId: Snowflake,
        token: String,
        wait: Boolean? = null,
        threadId: Snowflake? = null,
        builder: WebhookMessageCreateBuilder.() -> Unit
    ): DiscordMessage? {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.ExecuteWebhookPost) {
            webhookIdTokenWaitThreadId(webhookId, token, wait, threadId)
            val request = WebhookMessageCreateBuilder().apply(builder).toRequest()
            body(WebhookExecuteRequest.serializer(), request.request)
            request.files.forEach { file(it) }
        }
    }

    @KordExperimental
    suspend fun executeSlackWebhook(
        webhookId: Snowflake,
        token: String,
        body: JsonObject,
        wait: Boolean? = null,
        threadId: Snowflake? = null,
    ) = call(Route.ExecuteSlackWebhookPost) {
        webhookIdTokenWaitThreadId(webhookId, token, wait, threadId)
        body(JsonObject.serializer(), body)
    }

    @KordExperimental
    suspend fun executeGithubWebhook(
        webhookId: Snowflake,
        token: String,
        body: JsonObject,
        wait: Boolean? = null,
        threadId: Snowflake? = null,
    ) = call(Route.ExecuteGithubWebhookPost) {
        webhookIdTokenWaitThreadId(webhookId, token, wait, threadId)
        body(JsonObject.serializer(), body)
    }

    suspend fun getWebhookMessage(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake? = null,
    ): DiscordMessage = call(Route.GetWebhookMessage) {
        webhookIdTokenMessageIdThreadId(webhookId, token, messageId, threadId)
    }

    suspend inline fun editWebhookMessage(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake? = null,
        builder: WebhookMessageModifyBuilder.() -> Unit
    ): DiscordMessage {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.EditWebhookMessage) {
            webhookIdTokenMessageIdThreadId(webhookId, token, messageId, threadId)
            val body = WebhookMessageModifyBuilder().apply(builder).toRequest()
            body(WebhookEditMessageRequest.serializer(), body.request)
            body.files.orEmpty().onEach { file(it) }
        }
    }

    suspend fun deleteWebhookMessage(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake? = null,
    ) = call(Route.DeleteWebhookMessage) {
        webhookIdTokenMessageIdThreadId(webhookId, token, messageId, threadId)
    }
}

@PublishedApi
internal fun RequestBuilder<*>.webhookIdToken(webhookId: Snowflake, token: String) {
    keys[Route.WebhookId] = webhookId
    keys[Route.WebhookToken] = token
}

@PublishedApi
internal fun RequestBuilder<*>.webhookIdTokenWaitThreadId(
    webhookId: Snowflake,
    token: String,
    wait: Boolean?,
    threadId: Snowflake?,
) {
    webhookIdToken(webhookId, token)
    wait?.let { parameter("wait", it) }
    threadId?.let { parameter("thread_id", it) }
}

@PublishedApi
internal fun RequestBuilder<*>.webhookIdTokenMessageIdThreadId(
    webhookId: Snowflake,
    token: String,
    messageId: Snowflake,
    threadId: Snowflake?,
) {
    webhookIdToken(webhookId, token)
    keys[Route.MessageId] = messageId
    threadId?.let { parameter("thread_id", it) }
}
