package dev.kord.rest.service

import dev.kord.common.entity.DiscordGuildScheduledEvent
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.scheduled_events.ScheduledEventModifyBuilder
import dev.kord.rest.json.request.ScheduledEventModifyRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class ScheduledEventService(requestHandler: RequestHandler) : RestService(requestHandler) {
    suspend fun getScheduledEvent(eventId: Snowflake) = call(Route.GuildEventsGet) {
        keys[Route.ScheduledEventId] = eventId
    }

    suspend fun deleteScheduledEvent(eventId: Snowflake) = call(Route.GuildEventsDelete) {
        keys[Route.ScheduledEventId] = eventId
    }

    suspend fun modifyScheduledEvent(eventId: Snowflake, request: ScheduledEventModifyRequest) =
        call(Route.GuildEventsPatch) {
            keys[Route.ScheduledEventId] = eventId

            body(ScheduledEventModifyRequest.serializer(), request)
        }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun modifyScheduledEvent(
        eventId: Snowflake,
        builder: ScheduledEventModifyBuilder.() -> Unit
    ): DiscordGuildScheduledEvent {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        val appliedBuilder = ScheduledEventModifyBuilder().apply(builder)

        return modifyScheduledEvent(eventId, appliedBuilder.toRequest())
    }
}
