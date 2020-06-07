package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.*
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.cache.data.PermissionOverwriteData
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.rest.request.RestRequestException

class PermissionOverwriteEntity(
        val guildId: Snowflake,
        val channelId: Snowflake,
        data: PermissionOverwriteData,
        override val kord: Kord,
        override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
) : PermissionOverwrite(data), KordObject, Strategizable {

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    val channel: GuildChannelBehavior get() = GuildChannelBehavior(guildId, channelId, kord)

    /**
     * Requests to get the channel this overwrite applies to through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [GuildChannel] wasn't present.
     */
    suspend fun getChannel(): GuildChannel = strategy.supply(kord).getChannelOf(channelId)

    /**
     * Requests to get the channel this overwrite applies to through the [strategy],
     * returns null if the [GuildChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getChannelOrNull(): GuildChannel? = strategy.supply(kord).getChannelOfOrNull(channelId)

    /**
     * Requests to get the the guild of this overwrite through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    suspend fun getGuild(): Guild = strategy.supply(kord).getGuild(guildId)

    /**
     * Requests to get the guild of this overwrite through the [strategy],
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = strategy.supply(kord).getGuild(guildId)

    /**
     * Requests to delete this overwrite.
     *
     * @param reason an optional reason to be logged in the audit log.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete(reason: String? = null) {
        kord.rest.channel.deleteChannelPermission(channelId.value, data.id.toString(), reason)
    }

    /**
     * Returns a new [PermissionOverwriteEntity] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): PermissionOverwriteEntity =
            PermissionOverwriteEntity(guildId, channelId, data, kord, strategy)

}
