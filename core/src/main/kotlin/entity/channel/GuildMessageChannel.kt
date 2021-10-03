package dev.kord.core.entity.channel

import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.supplier.EntitySupplyStrategy

public interface GuildMessageChannel : GuildChannel, MessageChannel, GuildMessageChannelBehavior {

    /**
     * Returns a new [GuildMessageChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildMessageChannel

}
