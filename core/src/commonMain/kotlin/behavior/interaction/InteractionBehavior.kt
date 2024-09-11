package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/** The behavior of an [Interaction]. */
public interface InteractionBehavior : KordEntity, Strategizable {

    /** The id of the application the interaction is for. */
    public val applicationId: Snowflake

    /** A continuation token for responding to the interaction. */
    public val token: String

    /** The id of the channel the interaction was sent from. */
    public val channelId: Snowflake

    /** The behavior of the channel the interaction was sent from. */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    /**
     * Requests the [Channel] for the interaction as a [MessageChannel], returns null if the
     * channel isn't present or if the channel is not a [MessageChannel].
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     */
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests the [Channel] for the interaction as a [MessageChannel].
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     * @throws EntityNotFoundException if the channel is null.
     * @throws ClassCastException if the returned Channel is not of type [MessageChannel].
     */
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InteractionBehavior
}
