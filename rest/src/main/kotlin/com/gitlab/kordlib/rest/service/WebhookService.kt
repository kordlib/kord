package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.annotation.KordExperimental
import com.gitlab.kordlib.common.entity.DiscordMessage
import com.gitlab.kordlib.common.entity.DiscordWebhook
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.webhook.ExecuteWebhookBuilder
import com.gitlab.kordlib.rest.builder.webhook.WebhookCreateBuilder
import com.gitlab.kordlib.rest.builder.webhook.WebhookModifyBuilder
import com.gitlab.kordlib.rest.json.request.WebhookCreateRequest
import com.gitlab.kordlib.rest.json.request.WebhookExecuteRequest
import com.gitlab.kordlib.rest.json.request.WebhookModifyRequest
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route
import kotlinx.serialization.json.JsonObject
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class WebhookService(requestHandler: RequestHandler) : RestService(requestHandler) {

    @OptIn(ExperimentalContracts::class)
    suspend inline fun createWebhook(channelId: Snowflake, name: String, builder: WebhookCreateBuilder.() -> Unit): DiscordWebhook {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.WebhookPost) {
            keys[Route.ChannelId] = channelId
            val createBuilder = WebhookCreateBuilder(name).apply(builder)
            body(WebhookCreateRequest.serializer(), createBuilder.toRequest())
            createBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
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
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyWebhook(webhookId: Snowflake, builder: WebhookModifyBuilder.() -> Unit): DiscordWebhook {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.WebhookPatch) {
            keys[Route.WebhookId] = webhookId
            val modifyBuilder = WebhookModifyBuilder().apply(builder)
            body(WebhookModifyRequest.serializer(), modifyBuilder.toRequest())
            modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyWebhookWithToken(webhookId: Snowflake, token: String, builder: WebhookModifyBuilder.() -> Unit): DiscordWebhook {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.WebhookByTokenPatch) {
            keys[Route.WebhookId] = webhookId
            keys[Route.WebhookToken] = token
            val modifyBuilder = WebhookModifyBuilder().apply(builder)
            body(WebhookModifyRequest.serializer(), modifyBuilder.toRequest())
            modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
        }
    }

    suspend fun deleteWebhook(webhookId: Snowflake, reason: String? = null) = call(Route.WebhookDelete) {
        keys[Route.WebhookId] = webhookId
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend fun deleteWebhookWithToken(webhookId: Snowflake, token: String, reason: String? = null) = call(Route.WebhookByTokenDelete) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun executeWebhook(webhookId: Snowflake, token: String, wait: Boolean, builder: ExecuteWebhookBuilder.() -> Unit): DiscordMessage? {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return call(Route.ExecuteWebhookPost) {
            keys[Route.WebhookId] = webhookId
            keys[Route.WebhookToken] = token
            parameter("wait", "$wait")
            val request = ExecuteWebhookBuilder().apply(builder).toRequest()
            body(WebhookExecuteRequest.serializer(), request.request)
            request.file?.let { file(it) }
        }
    }

    @KordExperimental
    suspend fun executeSlackWebhook(webhookId: Snowflake, token: String, body: JsonObject, wait: Boolean = false) =
            call(Route.ExecuteSlackWebhookPost) {
                keys[Route.WebhookId] = webhookId
                keys[Route.WebhookToken] = token
                parameter("wait", "$wait")
                body(JsonObject.serializer(), body)
            }

    @KordExperimental
    suspend fun executeGithubWebhook(webhookId: Snowflake, token: String, body: JsonObject, wait: Boolean = false) =
            call(Route.ExecuteGithubWebhookPost) {
                keys[Route.WebhookId] = webhookId
                keys[Route.WebhookToken] = token
                parameter("wait", "$wait")
                body(JsonObject.serializer(), body)
            }
}