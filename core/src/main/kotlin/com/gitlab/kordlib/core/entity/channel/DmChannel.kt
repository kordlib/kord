package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * An instance of a Discord DM channel.
 */
data class DmChannel(override val data: ChannelData, override val kord: Kord, override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
) : MessageChannel {
    /**
     * The ids of the recipients of the channel.
     */
    val recipientIds: Set<Snowflake> get() = data.recipients.orEmpty().asSequence().map { Snowflake(it) }.toSet()

    /**
     * Requests to get the recipients of the channel.
     */
    val recipients: Flow<User> get() = recipientIds.asFlow().map { strategy.supply(kord).getUser(it) }.filterNotNull()

}