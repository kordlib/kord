package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.ThreadChannelBehavior
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

interface ThreadUserBehavior : UserBehavior {

    val threadId: Snowflake

    val thread: ThreadChannelBehavior get() = ThreadChannelBehavior(threadId, kord)

    suspend fun getThread(): ThreadChannel = supplier.getChannelOf(threadId)

    suspend fun getThreadOrNull(): ThreadChannel? = supplier.getChannelOfOrNull(threadId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): UserBehavior {
        return ThreadUserBehavior(id, threadId, kord, strategy.supply(kord))

    }
}

fun ThreadUserBehavior(
    id: Snowflake,
    threadId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): ThreadUserBehavior {
    return object : ThreadUserBehavior {
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