package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.annotation.KordExperimental
import com.gitlab.kordlib.rest.json.request.MultiPartWebhookExecutePostRequest
import com.gitlab.kordlib.rest.json.request.WebhookCreatePostRequest
import com.gitlab.kordlib.rest.json.request.WebhookExecutePostRequest
import com.gitlab.kordlib.rest.json.request.WebhookModifyPatchRequest
import com.gitlab.kordlib.rest.ratelimit.RequestHandler
import com.gitlab.kordlib.rest.route.Route
import kotlinx.serialization.json.JsonObject

class WebhookService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun createWebhook(channelId: String, webhook: WebhookCreatePostRequest, reason: String? = null) = call(Route.WebhookPost) {
        keys[Route.ChannelId] = channelId
        body(WebhookCreatePostRequest.serializer(), webhook)
        reason?.let { header("X-Audit-Log-Reason", it) }
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

    suspend fun modifyWebhook(webhookId: String, webhook: WebhookModifyPatchRequest, reason: String? = null) = call(Route.WebhookPatch) {
        keys[Route.WebhookId] = webhookId
        body(WebhookModifyPatchRequest.serializer(), webhook)
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun modifyWebhookWithToken(webhookId: String, token: String, webhook: WebhookModifyPatchRequest) = call(Route.WebhookByTokenPatch) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        body(WebhookModifyPatchRequest.serializer(), webhook)
    }

    suspend fun deleteWebhook(webhookId: String, reason: String? = null) = call(Route.WebhookDelete) {
        keys[Route.WebhookId] = webhookId
        reason?.let { header("X-Audit-Log-Reason", it) }
    }

    suspend fun deleteWebhookWithToken(webhookId: String, token: String) = call(Route.WebhookByTokenDelete) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
    }

    suspend fun executeWebhook(webhookId: String, token: String, wait: Boolean, webhook: MultiPartWebhookExecutePostRequest) = call(Route.ExecuteWebhookPost) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        parameter("wait", "$wait")
        body(WebhookExecutePostRequest.serializer(), webhook.request)
        webhook.files.forEach { file(it) }
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