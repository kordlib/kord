package dev.kord.core.behavior.channel.threads

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
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
    /**
     * Requests to get all members of the current thread.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val members: Flow<ThreadUser>
        get() = supplier.getThreadMembers(id)

    /**
     * Removes the user identified by [id] from the current thread.
     * Requires the thread is not locked.
     * or current bot has [Manage Threads][dev.kord.common.entity.Permission.ManageThreads] permission
     *
     * @see [ThreadChannel.isArchived]
     * @see [ThreadChannel.isLocked]
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun removeUser(userId: Snowflake) {
        kord.rest.channel.removeUserFromThread(id, userId)
    }

    /**
     * Adds the user identified by [id] from the current thread.
     * Requires the thread is not locked.
     * or current bot has [Manage Threads][dev.kord.common.entity.Permission.ManageThreads] permission
     *
     * @see [ThreadChannel.isArchived]
     * @see [ThreadChannel.isLocked]
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun addUser(userId: Snowflake) {
        kord.rest.channel.addUserToThread(id, userId)
    }

    /**
     * Join the the current thread.
     * Requires the thread is not locked.
     * or current bot has [Manage Threads][dev.kord.common.entity.Permission.ManageThreads] permission
     *
     * @see [ThreadChannel.isArchived]
     * @see [ThreadChannel.isLocked]
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun join() {
        kord.rest.channel.joinThread(id)
    }

    /**
     * Leaves the current thread if the bot has already joined.
     * Requires the thread is not locked.
     * or current bot has [Manage Threads][dev.kord.common.entity.Permission.ManageThreads] permission
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun leave() {
        kord.rest.channel.leaveThread(id)
    }

    /**
     * Deleting a thread requires the [Manage Threads][dev.kord.common.entity.Permission.ManageThreads] permission.
     */
    override suspend fun delete() {
        super.delete()
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

/**
 * * Editing a thread to set [archived][ThreadChannel.isArchived] to false only requires the current user to be in the thread.
 * * If [locked][ThreadChannel.isLocked] is true, then the user must have [Manage Threads][dev.kord.common.entity.Permission.ManageThreads]
 * * Editing a thread to change the
 * [name][ThreadModifyBuilder.name],
 * [archived][ThreadModifyBuilder.archived],
 * [autoArchiveDuration][ThreadModifyBuilder.autoArchiveDuration] fields
 * requires [Manage Threads][dev.kord.common.entity.Permission.ManageThreads] or that the current user is the thread creator.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun ThreadChannelBehavior.edit(builder: ThreadModifyBuilder.() -> Unit): ThreadChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val appliedBuilder = ThreadModifyBuilder().apply(builder)
    val patchedChannel = kord.rest.channel.patchThread(id, appliedBuilder.toRequest(), appliedBuilder.reason)
    return Channel.from(patchedChannel.toData(), kord) as ThreadChannel
}

internal fun ThreadChannelBehavior(
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