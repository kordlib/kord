package dev.kord.core.behavior.channel.threads

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.entity.channel.thread.ThreadUser
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.thread.ThreadModifyBuilder
import kotlinx.coroutines.flow.Flow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface ThreadChannelBehavior : MessageChannelBehavior {

    val members: Flow<ThreadUser>
        get() = supplier.getThreadMembers(id)


    suspend fun removeUser(userId: Snowflake) {
        kord.rest.channel.removeUserFromThread(id, userId)
    }

    suspend fun addUser(userId: Snowflake) {
        kord.rest.channel.addUserToThread(id, userId)
    }

    suspend fun join() {
        kord.rest.channel.joinThread(id)
    }

    suspend fun leave() {
        kord.rest.channel.leaveThread(id)
    }

    override suspend fun asChannel(): ThreadChannel {
        return super.asChannel() as ThreadChannel
    }

    override suspend fun asChannelOrNull(): ThreadChannel? {
        return super.asChannelOrNull() as ThreadChannel
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ThreadChannelBehavior {
        return ThreadChannelBehavior(id, kord, strategy.supply(kord))
    }

}

@OptIn(ExperimentalContracts::class)
suspend inline fun ThreadChannelBehavior.edit(builder: ThreadModifyBuilder.() -> Unit): ThreadChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val appliedBuilder = ThreadModifyBuilder().apply(builder)
    val patchedChannel = kord.rest.channel.patchThread(id, appliedBuilder.toRequest(), appliedBuilder.reason)
    return Channel.from(patchedChannel.toData(), kord) as ThreadChannel
}

fun ThreadChannelBehavior(
    id: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): ThreadChannelBehavior {
    return object : ThreadChannelBehavior {
        override val kord: Kord
            get() = kord
        override val id: Snowflake
            get() = id

        override val supplier: EntitySupplier
            get() = supplier

    }
}