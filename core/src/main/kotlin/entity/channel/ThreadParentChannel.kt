package dev.kord.core.entity.channel

import dev.kord.common.entity.ArchiveDuration
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

interface ThreadParentChannel : ThreadParentChannelBehavior, GuildMessageChannel {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadParentChannel
}