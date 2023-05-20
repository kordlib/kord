package dev.kord.rest.service

import dev.kord.common.entity.DiscordStageInstance
import dev.kord.common.entity.Snowflake
import dev.kord.rest.ById
import dev.kord.rest.builder.stage.StageInstanceCreateBuilder
import dev.kord.rest.builder.stage.StageInstanceModifyBuilder
import dev.kord.rest.json.request.StageInstanceCreateRequest
import dev.kord.rest.json.request.StageInstanceModifyRequest
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class StageInstanceService(public val client: HttpClient) {

    public suspend fun getStageInstance(channelId: Snowflake): DiscordStageInstance =
        client.get(Routes.Channels.ById(channelId)).body()

    public suspend fun createStageInstance(
        request: StageInstanceCreateRequest,
        reason: String? = null,
    ): DiscordStageInstance =
        client.post(Routes.Channels) {
        setBody(request)
        auditLogReason(reason)
    }.body()

    public suspend inline fun createStageInstance(
        channelId: Snowflake,
        topic: String,
        builder: StageInstanceCreateBuilder.() -> Unit = {},
    ): DiscordStageInstance {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        val appliedBuilder = StageInstanceCreateBuilder(channelId, topic).apply(builder)
        return createStageInstance(appliedBuilder.toRequest(), appliedBuilder.reason)
    }

    public suspend fun modifyStageInstance(
        channelId: Snowflake,
        request: StageInstanceModifyRequest,
        reason: String? = null,
    ): DiscordStageInstance = client.patch(Routes.Channels.ById(channelId)) {
        setBody(request)
        auditLogReason(reason)
    }.body()

    public suspend inline fun modifyStageInstance(
        channelId: Snowflake,
        builder: StageInstanceModifyBuilder.() -> Unit,
    ): DiscordStageInstance {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

        val appliedBuilder = StageInstanceModifyBuilder().apply(builder)
        return modifyStageInstance(channelId, appliedBuilder.toRequest(), appliedBuilder.reason)
    }

    public suspend fun deleteStageInstance(channelId: Snowflake, reason: String? = null): Unit =
        client.delete(Routes.Channels.ById(channelId)) {
            auditLogReason(reason)
        }.body()
}
