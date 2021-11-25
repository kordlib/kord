package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

public interface InteractionBehavior : KordEntity, Strategizable {
    public val applicationId: Snowflake
    public val token: String
    public val channelId: Snowflake

    /**
     * The [MessageChannelBehavior] of the channel the command was executed in.
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InteractionBehavior =
        InteractionBehavior(id, channelId, token, applicationId, kord, strategy)
}
