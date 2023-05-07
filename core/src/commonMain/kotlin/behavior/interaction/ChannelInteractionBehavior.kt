package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

public interface ChannelInteractionBehavior : InteractionBehavior {
    override val channelId: Snowflake

    override val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    @Deprecated(
        "Discord no longer provides a non-null variant channel",
        ReplaceWith("getChannelOrNull()"),
        DeprecationLevel.WARNING
    )
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ChannelInteractionBehavior
}