package dev.kord.core.behavior.interaction.followup

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.followup.FollowupMessage
import dev.kord.core.entity.interaction.followup.PublicFollowupMessage
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.builder.message.modify.FollowupMessageModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Followup Message](https://discord.com/developers/docs/interactions/receiving-and-responding#followup-messages)
 */
public interface FollowupMessageBehavior : KordEntity, Strategizable {

    /** The ID of the application making the request. */
    public val applicationId: Snowflake

    /** The continuation token for responding to the interaction. */
    public val token: String

    /** The ID of the channel the followup message was within. */
    public val channelId: Snowflake

    /** The channel as a [MessageChannelBehavior] object. */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    /**
     * Requests the channel as a [MessageChannel].
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     * @throws EntityNotFoundException if the channel is null.
     * @throws ClassCastException if the returned Channel is not of type [MessageChannel].
     */
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests the channel as a [MessageChannel], returns null if the channel isn't present or if the channel is not a
     * [MessageChannel]
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     */
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to delete this followup message.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete() {
        kord.rest.interaction.deleteFollowupMessage(applicationId, token, id)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): FollowupMessageBehavior
}

/**
 * Requests to edit this followup message.
 *
 * @return The edited [FollowupMessage] of the interaction response, either [public][PublicFollowupMessage] or
 * [ephemeral][EphemeralFollowupMessage].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun FollowupMessageBehavior.edit(
    builder: FollowupMessageModifyBuilder.() -> Unit,
): FollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder)

    return FollowupMessage(Message(response.toData(), kord), applicationId, token, kord)
}
