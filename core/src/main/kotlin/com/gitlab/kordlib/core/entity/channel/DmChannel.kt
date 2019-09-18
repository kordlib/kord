package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * An instance of a Discord DM channel.
 */
data class DmChannel(override val data: ChannelData, override val kord: Kord) : MessageChannel {

    /**
     * The ids of the recipients of the channel.
     */
    val recipientIds: Set<Snowflake> get() = data.recipients.orEmpty().asSequence().map { Snowflake(it) }.toSet()

    /**
     * Requests to get the recipients of the channel.
     */
    val recipients: Flow<User> get() = recipientIds.asFlow().map { kord.getUser(it) }.filterNotNull()

}