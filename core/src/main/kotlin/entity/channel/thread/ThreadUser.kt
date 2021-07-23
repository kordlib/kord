package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.threads.ChannelThreadBehavior
import dev.kord.core.cache.data.ThreadUserData
import dev.kord.core.entity.Strategizable
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

class ThreadUser(
    val data: ThreadUserData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
    ) : KordObject, Strategizable {
    val userId get() = data.userId.value

    val user: UserBehavior? get() = userId?.let { UserBehavior(it, kord) }

    val threadId: Snowflake? get() = data.id.value

    val thread: ChannelThreadBehavior? get() = threadId?.let { ChannelThreadBehavior(it, kord) }

    val joinTimestamp: String get() = data.joinTimestamp

    val flags: Int = data.flags

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable {
        return ThreadUser(data, kord, strategy.supply(kord))
    }
}
