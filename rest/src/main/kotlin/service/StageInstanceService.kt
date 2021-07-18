package dev.kord.rest.service

import dev.kord.common.entity.DiscordStageInstance
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.StageInstanceCreateRequest
import dev.kord.rest.json.request.StageInstanceUpdateRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.request.auditLogReason
import dev.kord.rest.route.Route

class StageInstanceService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun getStageInstance(channelId: Snowflake): DiscordStageInstance = call(Route.StageInstanceGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun createStageInstance(request: StageInstanceCreateRequest, reason: String? = null): DiscordStageInstance =
        call(Route.StageInstancePost) {
            body(StageInstanceCreateRequest.serializer(), request)
            auditLogReason(reason)
        }

    suspend fun updateStageInstance(channelId: Snowflake, request: StageInstanceUpdateRequest, reason: String? = null): DiscordStageInstance =
        call(Route.StageInstancePost) {
            keys[Route.ChannelId] = channelId

            body(StageInstanceUpdateRequest.serializer(), request)
            auditLogReason(reason)
        }

    suspend fun deleteStageInstance(channelId: Snowflake, reason: String? = null): Unit = call(Route.StageInstanceDelete) {
        keys[Route.ChannelId] = channelId
        auditLogReason(reason)
    }
}

suspend fun StageInstanceService.createStageInstance(channelId: Snowflake, topic: String, reason: String? = null) = createStageInstance(
    StageInstanceCreateRequest(channelId, topic), reason
)

suspend fun StageInstanceService.updateStageInstance(channelId: Snowflake, topic: String, reason: String? = null) = updateStageInstance(
    channelId,
    StageInstanceUpdateRequest(topic),
    reason
)
