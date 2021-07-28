package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.ThreadUserBehavior
import dev.kord.core.cache.data.ThreadUserData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

class ThreadUser(
    val data: ThreadUserData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ThreadUserBehavior {
    override val id: Snowflake
        get() = data.userId.orElse(kord.selfId)

    override val threadId: Snowflake get() = data.id


    val joinTimestamp: String get() = data.joinTimestamp

    val flags: Int = data.flags


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadUser {
        return ThreadUser(data, kord, strategy.supply(kord))
    }
}
