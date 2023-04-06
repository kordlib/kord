package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

public interface ThreadMemberBehavior : UserBehavior {

    public val threadId: Snowflake

    public suspend fun getThread(): ThreadChannel = supplier.getChannelOf(threadId)

    public suspend fun getThreadOrNull(): ThreadChannel? = supplier.getChannelOfOrNull(threadId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): UserBehavior {
        return ThreadMemberBehavior(id, threadId, kord, strategy.supply(kord))

    }
}

public fun ThreadMemberBehavior(
    id: Snowflake,
    threadId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): ThreadMemberBehavior {
    return object : ThreadMemberBehavior {
        override val id: Snowflake
            get() = id
        override val threadId: Snowflake
            get() = threadId
        override val kord: Kord
            get() = kord
        override val supplier: EntitySupplier
            get() = supplier
    }
}
