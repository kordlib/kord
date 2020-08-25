package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.annotation.KordExperimental
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.rest.builder.webhook.ExecuteWebhookBuilder
import com.gitlab.kordlib.rest.builder.webhook.WebhookCreateBuilder
import com.gitlab.kordlib.rest.builder.webhook.WebhookModifyBuilder
import com.gitlab.kordlib.rest.json.request.WebhookCreateRequest
import com.gitlab.kordlib.rest.json.request.WebhookExecuteRequest
import com.gitlab.kordlib.rest.json.request.WebhookModifyRequest
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.route.Route
import kotlinx.serialization.json.JsonObject

class WebhookService(requestHandler: RequestHandler) : RestService(requestHandler) {

    @KordUnstableApi
    suspend inline fun createWebhook(channelId: String, builder: WebhookCreateBuilder.() -> Unit) = call(Route.WebhookPost) {
        keys[Route.ChannelId] = channelId
        val createBuilder = WebhookCreateBuilder().apply(builder)
        body(WebhookCreateRequest.serializer(), createBuilder.toRequest())
        createBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun getChannelWebhooks(channelId: String) = call(Route.ChannelWebhooksGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun getGuildWebhooks(guildId: String) = call(Route.GuildWebhooksGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun getWebhook(webhookId: String) = call(Route.WebhookGet) {
        keys[Route.WebhookId] = webhookId
    }

    suspend fun getWebhookWithToken(webhookId: String, token: String) = call(Route.WebhookByTokenGet) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
    }

    @KordUnstableApi
    suspend inline fun modifyWebhook(webhookId: String, builder: WebhookModifyBuilder.() -> Unit) = call(Route.WebhookPatch) {
        keys[Route.WebhookId] = webhookId
        val modifyBuilder = WebhookModifyBuilder().apply(builder)
        body(WebhookModifyRequest.serializer(), modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    @KordUnstableApi
    suspend inline fun modifyWebhookWithToken(webhookId: String, token: String, builder: WebhookModifyBuilder.() -> Unit) = call(Route.WebhookByTokenPatch) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        val modifyBuilder = WebhookModifyBuilder().apply(builder)
        body(WebhookModifyRequest.serializer(), modifyBuilder.toRequest())
        modifyBuilder.reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteWebhook(webhookId: String, reason: String? = null) = call(Route.WebhookDelete) {
        keys[Route.WebhookId] = webhookId
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    suspend fun deleteWebhookWithToken(webhookId: String, token: String, reason: String? = null) = call(Route.WebhookByTokenDelete) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        reason?.let { header("X-Audit-Log-Reason", reason) }
    }

    @KordUnstableApi
    suspend inline fun executeWebhook(webhookId: String, token: String, wait: Boolean, builder: ExecuteWebhookBuilder.() -> Unit) = call(Route.ExecuteWebhookPost) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        parameter("wait", "$wait")
        val request = ExecuteWebhookBuilder().apply(builder).toRequest()
        body(WebhookExecuteRequest.serializer(), request.request)
        request.file?.let { file(it) }
    }

    @KordExperimental
    suspend fun executeSlackWebhook(webhookId: String, token: String, body: JsonObject, wait: Boolean = false) =
            call(Route.ExecuteSlackWebhookPost) {
                keys[Route.WebhookId] = webhookId
                keys[Route.WebhookToken] = token
                parameter("wait", "$wait")
                body(JsonObject.serializer(), body)
            }

    @KordExperimental
    suspend fun executeGithubWebhook(webhookId: String, token: String, body: JsonObject, wait: Boolean = false) =
            call(Route.ExecuteGithubWebhookPost) {
                keys[Route.WebhookId] = webhookId
                keys[Route.WebhookToken] = token
                parameter("wait", "$wait")
                body(JsonObject.serializer(), body)
            }
}