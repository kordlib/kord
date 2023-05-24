package dev.kord.rest.service

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.DiscordMessage
import dev.kord.common.entity.DiscordWebhook
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.rest.*
import dev.kord.rest.builder.message.create.WebhookMessageCreateBuilder
import dev.kord.rest.builder.message.modify.WebhookMessageModifyBuilder
import dev.kord.rest.builder.webhook.WebhookCreateBuilder
import dev.kord.rest.builder.webhook.WebhookModifyBuilder
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.request.setBodyWithFiles
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonObject
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class WebhookService(public val client: HttpClient) {

    public suspend inline fun createWebhook(
        channelId: Snowflake,
        name: String,
        builder: WebhookCreateBuilder.() -> Unit
    ): DiscordWebhook {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return client.post(Routes.Channels.ById.Webhooks(channelId)) {
            val createBuilder = WebhookCreateBuilder(name).apply(builder)
            setBody(createBuilder.toRequest())
            auditLogReason(createBuilder.reason)
        }.body()
    }

    public suspend fun getChannelWebhooks(channelId: Snowflake): List<DiscordWebhook> =
        client.get(Routes.Channels.ById.Webhooks(channelId)).body()

    public suspend fun getGuildWebhooks(guildId: Snowflake): List<DiscordWebhook> =
        client.get(Routes.Guilds.ById.Webhooks(guildId)).body()

    public suspend fun getWebhook(webhookId: Snowflake): DiscordWebhook =
        client.get(Routes.Webhooks.ById(webhookId)).body()

    public suspend fun getWebhookWithToken(webhookId: Snowflake, token: String): DiscordWebhook =
        client.get(Routes.Webhooks.ById.WithToken(webhookId, token)).body()
    public suspend inline fun modifyWebhook(
        webhookId: Snowflake,
        builder: WebhookModifyBuilder.() -> Unit,
    ): DiscordWebhook {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return client.patch(Routes.Webhooks.ById(webhookId)) {
            val modifyBuilder = WebhookModifyBuilder().apply(builder)
            setBody(modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }.body()
    }

    public suspend inline fun modifyWebhookWithToken(
        webhookId: Snowflake,
        token: String,
        builder: WebhookModifyBuilder.() -> Unit
    ): DiscordWebhook {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return client.patch(Routes.Webhooks.ById.WithToken(webhookId, token)) {
            webhookIdToken(webhookId, token)
            val modifyBuilder = WebhookModifyBuilder().apply(builder)
            setBody(modifyBuilder.toRequest())
            auditLogReason(modifyBuilder.reason)
        }.body()
    }

    public suspend fun deleteWebhook(webhookId: Snowflake, reason: String? = null) {
        client.delete(Routes.Webhooks.ById(webhookId)) {
            auditLogReason(reason)
        }
    }

    public suspend fun deleteWebhookWithToken(webhookId: Snowflake, token: String, reason: String? = null): Unit {
        client.delete(Routes.Webhooks.ById.WithToken(webhookId, token)) {
            auditLogReason(reason)
        }
    }

    public suspend inline fun executeWebhook(
        webhookId: Snowflake,
        token: String,
        wait: Boolean? = null,
        threadId: Snowflake? = null,
        builder: WebhookMessageCreateBuilder.() -> Unit
    ): DiscordMessage? {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return client.post(Routes.Webhooks.ById.WithToken(webhookId, token)) {
            val executeBuilder = WebhookMessageCreateBuilder().apply(builder)
            setBodyWithFiles(executeBuilder.toRequest(), executeBuilder.files)
        }.body()
    }

    @KordExperimental
    public suspend fun executeSlackWebhook(
        webhookId: Snowflake,
        token: String,
        request: JsonObject,
        wait: Boolean? = null,
        threadId: Snowflake? = null,
    ): Unit =
        client.post(Routes.Webhooks.ById.WithToken.Slack(webhookId, token,)) {
        webhookIdTokenWaitThreadId(webhookId, token, wait, threadId)
        setBody(request)
    }.body()

    @KordExperimental
    public suspend fun executeGithubWebhook(
        webhookId: Snowflake,
        token: String,
        request: JsonObject,
        wait: Boolean? = null,
        threadId: Snowflake? = null,
    ): Unit = client.post(Routes.Webhooks.ById.WithToken.Github(webhookId, token)) {
        webhookIdTokenWaitThreadId(webhookId, token, wait, threadId)
        setBody(request)
    }.body()

    public suspend fun getWebhookMessage(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake? = null,
    ): DiscordMessage =
        client.get(Routes.Webhooks.ById.WithToken.Messages.ById(webhookId, token, messageId)) {
        webhookIdTokenMessageIdThreadId(webhookId, token, messageId, threadId)
    }.body()

    public suspend inline fun editWebhookMessage(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake? = null,
        builder: WebhookMessageModifyBuilder.() -> Unit
    ): DiscordMessage {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        return client.patch(Routes.Webhooks.ById.WithToken.Messages.ById(webhookId, token, messageId)) {
            webhookIdTokenMessageIdThreadId(webhookId, token, messageId, threadId)
            val requestBuilder = WebhookMessageModifyBuilder().apply(builder)
            setBodyWithFiles(requestBuilder.toRequest(), requestBuilder.files.orEmpty())
        }.body()
    }

    public suspend fun deleteWebhookMessage(
        webhookId: Snowflake,
        token: String,
        messageId: Snowflake,
        threadId: Snowflake? = null,
    ): Unit {
        client.delete(Routes.Webhooks.ById.WithToken.Messages.ById(webhookId, token, messageId)) {
            webhookIdTokenMessageIdThreadId(webhookId, token, messageId, threadId)
        }
    }
}

@PublishedApi
internal fun HttpRequestBuilder.webhookIdToken(webhookId: Snowflake, token: String) {
}

@PublishedApi
internal fun HttpRequestBuilder.webhookIdTokenWaitThreadId(
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
internal fun HttpRequestBuilder.webhookIdTokenMessageIdThreadId(
    webhookId: Snowflake,
    token: String,
    messageId: Snowflake,
    threadId: Snowflake?,
) {
    webhookIdToken(webhookId, token)
    threadId?.let { parameter("thread_id", it) }
}
