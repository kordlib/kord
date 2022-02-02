package dev.kord.core.entity.channel

import dev.kord.core.cache.data.InviteData
import dev.kord.core.entity.Invite
import dev.kord.rest.builder.channel.InviteCreateBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface InviteChannel : TopGuildChannel

/**
 * Request to create an invite for this channel.
 *
 * @return the created [Invite].
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun InviteChannel.createInvite(builder: InviteCreateBuilder.() -> Unit = {}): Invite {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.createInvite(id, builder)
    val data = InviteData.from(response)

    return Invite(data, kord)
}
