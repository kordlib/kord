package dev.kord.rest.service

import dev.kord.common.entity.DiscordMessage
import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.response.AnswerVotersGetResponse
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

public class PollService(requestHandler: RequestHandler) : RestService(requestHandler) {
    public suspend fun getAnswerVoters(
        channelId: Snowflake,
        messageId: Snowflake,
        answerId: Int,
        after: Snowflake? = null,
        limit: Int? = null
    ): AnswerVotersGetResponse = call(Route.AnswerVotersGet) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
        keys[Route.PollAnswerId] = answerId.toString()

        if (after != null) {
            parameter("after", after)
        }

        if (limit != null) {
            parameter("limit", limit)
        }
    }

    public suspend fun endPoll(
        channelId: Snowflake,
        messageId: Snowflake,
    ): DiscordMessage = call(Route.PollEnd) {
        keys[Route.ChannelId] = channelId
        keys[Route.MessageId] = messageId
    }
}