package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.ThreadMemberBehavior
import dev.kord.core.cache.data.ThreadMemberData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

class ThreadMember(
    val data: ThreadMemberData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadMemberBehavior {
    override val id: Snowflake
        get() = data.userId.orElse(kord.selfId)

    override val threadId: Snowflake get() = data.id


    val joinTimestamp: String get() = data.joinTimestamp

    val flags: Int = data.flags


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadMember {
        return ThreadMember(data, kord, strategy.supply(kord))
    }
}
