package dev.kord.core.behavior.interaction

import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

public interface ChannelInteractionBehavior : InteractionBehavior {
    /** The behavior of the channel the interaction was sent from. */
    public val channel: MessageChannelBehavior? get() = channelId?.let { MessageChannelBehavior(it, kord) }

    public suspend fun getChannelOrNull(): MessageChannel? = channelId?.let { supplier.getChannelOfOrNull(it) }

    @Deprecated(
        "Discord no longer provides a non-null variant channel",
        ReplaceWith("getChannelOrNull()"),
        DeprecationLevel.WARNING
    )
    public suspend fun getChannel(): MessageChannel = channelId?.let { supplier.getChannelOf(it) }!!

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ChannelInteractionBehavior
}