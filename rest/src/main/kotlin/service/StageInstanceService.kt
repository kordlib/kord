package dev.kord.rest.service

import dev.kord.common.entity.DiscordStageInstance
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.StageInstanceCreateRequest
import dev.kord.rest.json.request.StageInstanceUpdateRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class StageInstanceService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun getStageInstance(channelId: Snowflake): DiscordStageInstance = call(Route.StageInstanceGet) {
        keys[Route.ChannelId] = channelId
    }

    suspend fun createStageInstance(request: StageInstanceCreateRequest): DiscordStageInstance =
        call(Route.StageInstancePost) {
            body(StageInstanceCreateRequest.serializer(), request)
        }

    suspend fun updateStageInstance(channelId: Snowflake, request: StageInstanceUpdateRequest): DiscordStageInstance =
        call(Route.StageInstancePost) {
            keys[Route.ChannelId] = channelId

            body(StageInstanceUpdateRequest.serializer(), request)
        }

    suspend fun deleteStageInstance(channelId: Snowflake): Unit = call(Route.StageInstanceDelete) {
        keys[Route.ChannelId] = channelId
    }
}

suspend fun StageInstanceService.createStageInstance(channelId: Snowflake, topic: String) = createStageInstance(
    StageInstanceCreateRequest(channelId, topic)
)

suspend fun StageInstanceService.updateStageInstance(channelId: Snowflake, topic: String) = updateStageInstance(
    channelId,
    StageInstanceUpdateRequest(topic)
)
