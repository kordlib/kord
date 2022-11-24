package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.ForumChannelThreadBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier

public class ForumChannelThread(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel, ForumChannelThreadBehavior {
    public val appliedTags: List<Snowflake>? get() = data.appliedTags.value
}