package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.Channel
import kotlinx.coroutines.flow.Flow

fun EntitySupplier.withFallback(other: EntitySupplier): EntitySupplier = FallbackEntitySupplier(this, other)

private class FallbackEntitySupplier(val first: EntitySupplier, val second: EntitySupplier) : EntitySupplier {

    override val guilds: Flow<Guild>
        get() = first.guilds.switchIfEmpty(second.guilds)

    override val regions: Flow<Region>
        get() = first.regions.switchIfEmpty(second.regions)

    override suspend fun getChannel(id: Snowflake): Channel? = first.getChannel(id) ?: second.getChannel(id)

    override suspend fun getGuild(id: Snowflake): Guild? = first.getGuild(id) ?: second.getGuild(id)

    override suspend fun getMember(guildId: Snowflake, userId: Snowflake): Member? =
            first.getMember(guildId = guildId, userId = userId) ?: second.getMember(guildId = guildId, userId = userId)

    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): Message? =
            first.getMessage(channelId = channelId, messageId = messageId) ?: second.getMessage(channelId = channelId, messageId = messageId)

    override suspend fun getSelf(): User? = first.getSelf() ?: second.getSelf()

    override suspend fun getUser(id: Snowflake): User? = first.getUser(id) ?: second.getUser(id)

}

