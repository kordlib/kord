package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * An instance of a Discord DM channel.
 */
data class DmChannel(override val data: ChannelData, override val kord: Kord) : MessageChannel {

    /**
     * The ids of the recipients of the channel.
     */
    val recipientIds: Set<Snowflake> get() = data.recipients.orEmpty().asSequence().map { Snowflake(it) }.toSet()

    /**
     * The behaviors of the recipients of the channel.
     */
    val recipientBehaviors: Set<UserBehavior> get() = recipientIds.map { UserBehavior(it, kord) }.toSet()

    /**
     * Requests to get the recipients of the channel.
     */
    val recipients: Flow<User> get() = recipientIds.asFlow().map { kord.getUser(it) }.filterNotNull()


    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when(other) {
        is ChannelBehavior -> other.id == id
        else -> false
    }
}