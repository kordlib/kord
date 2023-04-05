package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.Interaction
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

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InteractionBehavior
}
