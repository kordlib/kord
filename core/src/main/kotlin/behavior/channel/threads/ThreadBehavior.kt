package dev.kord.core.behavior.channel.threads

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.cache.data.ThreadUserData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.channel.thread.Thread
import dev.kord.core.entity.channel.thread.ThreadUser
import dev.kord.rest.builder.channel.thread.ThreadModifyBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface ThreadBehavior : GuildChannelBehavior {

    val members: Flow<ThreadUser>
        get() = flow {
            val threadUsers = supplier.getThreadMembersOrNull(id)

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

}

@OptIn(ExperimentalContracts::class)
suspend inline fun ThreadBehavior.edit(builder: ThreadModifyBuilder.() -> Unit): Thread {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val appliedBuilder = ThreadModifyBuilder().apply(builder)
    val patchedChannel = kord.rest.channel.patchThread(id, appliedBuilder.toRequest(), appliedBuilder.reason)
    val patchedChannelData = patchedChannel.toData()
    return Thread(patchedChannelData, kord)
}