package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.builder.message.modify.FollowupMessageModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Followup Message](https://discord.com/developers/docs/interactions/slash-commands#followup-messages)
 */

public interface FollowupMessageBehavior : KordEntity, Strategizable {

    public val applicationId: Snowflake
    public val token: String
    public val channelId: Snowflake

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

}

/**
 * Requests to edit this followup message.
 *
 * @return The edited [PublicFollowupMessage] of the interaction response.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun FollowupMessageBehavior.edit(builder: FollowupMessageModifyBuilder.() -> Unit): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder)
    return EphemeralFollowupMessage(Message(response.toData(), kord), applicationId, token, kord)
}
