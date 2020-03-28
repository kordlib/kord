package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import kotlinx.coroutines.flow.Flow

interface EntitySupplier {
    val guilds: Flow<Guild>

    val regions: Flow<Region>

    suspend fun getChannel(id: Snowflake): Channel?

    suspend fun getGuild(id: Snowflake): Guild?

    suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member?

    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message?

    suspend fun getSelf(): User?

    suspend fun getUser(id: Snowflake): User?
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
suspend inline fun <reified T : Channel> EntitySupplier.getChannel(id: Snowflake) = getChannel(id) as? T
