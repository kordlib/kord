package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.rest.json.request.CreateWebhookRequest
import com.gitlab.hopebaron.rest.json.request.ModifyWebhookRequest
import com.gitlab.hopebaron.rest.json.request.MultiPartWebhookExecuteRequest
import com.gitlab.hopebaron.rest.json.request.WebhookExecuteRequest
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.route.Route
import io.ktor.http.ParametersBuilder

class WebhookService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun createWebhook(channelId: String, webhook: CreateWebhookRequest) = call(Route.WebhookPost) {
        keys[Route.ChannelId] = channelId
        body(CreateWebhookRequest.serializer(), webhook)
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

    suspend fun modifyWebhook(webhookId: String, webhook: ModifyWebhookRequest) = call(Route.WebhookPatch) {
        keys[Route.WebhookId] = webhookId
        body(ModifyWebhookRequest.serializer(), webhook)
    }

    suspend fun modifyWebhook(webhookId: String, token: String, webhook: ModifyWebhookRequest) = call(Route.WebhookPatch) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        body(ModifyWebhookRequest.serializer(), webhook)
    }

    suspend fun deleteWebhook(webhookId: String) = call(Route.WebhookDelete) {
        keys[Route.WebhookId] = webhookId
    }

    suspend fun deleteWebhookWithToken(webhookId: String, token: String) = call(Route.WebhookDelete) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
    }

    suspend fun executeWebhook(webhookId: String, token: String, wait: Boolean, webhook: MultiPartWebhookExecuteRequest) = call(Route.ExecuteWebhookPost) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        parameters = with(ParametersBuilder()) {
            append("wait", "$wait")
            build()
        }
        body(WebhookExecuteRequest.serializer(), webhook.request)
        webhook.files.forEach { file(it) }
    }

    suspend fun executeSlackWebhook(webhookId: String, token: String, wait: Boolean) = call(Route.ExecuteSlackWebhookPost) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        parameters = with(ParametersBuilder()) {
            append("wait", "$wait")
            build()
        }
    }

    suspend fun executeGithubWebhook(webhookId: String, token: String, wait: Boolean) = call(Route.ExecuteGithubWebhookPost) {
        keys[Route.WebhookId] = webhookId
        keys[Route.WebhookToken] = token
        parameters = with(ParametersBuilder()) {
            append("wait", "$wait")
            build()
        }
    }
}