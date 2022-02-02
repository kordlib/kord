package dev.kord.rest.service

import dev.kord.common.entity.DiscordStageInstance
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.StageInstanceCreateRequest
import dev.kord.rest.json.request.StageInstanceUpdateRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Route

public class StageInstanceService(requestHandler: RequestHandler) : RestService(requestHandler) {

    public suspend fun getStageInstance(channelId: Snowflake): DiscordStageInstance = call(Route.StageInstanceGet) {
        keys[Route.ChannelId] = channelId
    }

    public suspend fun createStageInstance(
        request: StageInstanceCreateRequest,
        reason: String? = null,
    ): DiscordStageInstance = call(Route.StageInstancePost) {
        body(StageInstanceCreateRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend fun updateStageInstance(
        channelId: Snowflake,
        request: StageInstanceUpdateRequest,
        reason: String? = null,
    ): DiscordStageInstance = call(Route.StageInstancePost) {
        keys[Route.ChannelId] = channelId

        body(StageInstanceUpdateRequest.serializer(), request)
        auditLogReason(reason)
    }

    public suspend fun deleteStageInstance(channelId: Snowflake, reason: String? = null): Unit =
        call(Route.StageInstanceDelete) {
            keys[Route.ChannelId] = channelId
            auditLogReason(reason)
        }
}

public suspend fun StageInstanceService.createStageInstance(
    channelId: Snowflake,
    topic: String,
    reason: String? = null,
): DiscordStageInstance = createStageInstance(
    StageInstanceCreateRequest(channelId, topic), reason
)

public suspend fun StageInstanceService.updateStageInstance(
    channelId: Snowflake,
    topic: String,
    reason: String? = null,
): DiscordStageInstance = updateStageInstance(
    channelId,
    StageInstanceUpdateRequest(topic),
    reason
)
