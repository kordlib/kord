package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.ForumChannelThreadBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.Message
import dev.kord.core.supplier.EntitySupplier

public class ForumChannelThread(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadChannel, ForumChannelThreadBehavior {

    /**
     * Only available when creating a thread in a forum channel
     */
    public val message: Message get() = data.message.unwrap { Message(it, kord) }!!

    public val appliedTags: List<Snowflake>? get() = data.appliedTags.value

    override fun toString(): String {
        return "ForumChannelThread(data=$data, kord=$kord, supplier=$supplier)"
    }
}