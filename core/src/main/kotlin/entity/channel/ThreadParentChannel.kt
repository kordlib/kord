package dev.kord.core.entity.channel

import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.supplier.EntitySupplyStrategy

public interface ThreadParentChannel : ThreadParentChannelBehavior, TopGuildChannel {
    override suspend fun asChannel(): ThreadParentChannel {
        return super<ThreadParentChannelBehavior>.asChannel()
    }

    override suspend fun asChannelOrNull(): ThreadParentChannel? {
      return super<ThreadParentChannelBehavior>.asChannelOrNull()
    }

    override suspend fun fetchChannel(): ThreadParentChannel {
       return super<ThreadParentChannelBehavior>.fetchChannel()
    }

    override suspend fun fetchChannelOrNull(): ThreadParentChannel? {
       return super<ThreadParentChannelBehavior>.fetchChannelOrNull()
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadParentChannel
}
