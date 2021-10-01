package dev.kord.core.entity.channel

import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.supplier.EntitySupplyStrategy

public interface ThreadParentChannel : ThreadParentChannelBehavior, TopGuildMessageChannel {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadParentChannel
}
