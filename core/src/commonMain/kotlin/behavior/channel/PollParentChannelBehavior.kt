package dev.kord.core.behavior.channel

import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.DiscordPoll
import dev.kord.core.entity.Poll
import dev.kord.rest.builder.message.PollBuilder
import dev.kord.rest.builder.message.create.poll
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A channel behavior which allows for [poll creation][DiscordPoll].
 *
 * @see createPoll
 */
public interface PollParentChannelBehavior : MessageChannelBehavior

/**
 * Requests to create a poll.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(KordUnsafe::class)
public suspend inline fun PollParentChannelBehavior.createPoll(block: PollBuilder.() -> Unit): Poll {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return createMessage { poll(block) } as Poll
}