package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.cache.data.PermissionOverwriteData
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.request.RestRequestException

class PermissionOverwriteEntity(
    val guildId: Snowflake,
    val channelId: Snowflake,
    data: PermissionOverwriteData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : PermissionOverwrite(data), KordObject, Strategizable {

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    val channel: GuildChannelBehavior get() = GuildChannelBehavior(guildId, channelId, kord)

    /**
     * Requests to get the channel this overwrite applies to.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [GuildChannel] wasn't present.
     */
    suspend fun getChannel(): GuildChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel this overwrite applies to,
     * returns null if the [GuildChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getChannelOrNull(): GuildChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the the guild of this overwrite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild of this overwrite,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to delete this overwrite.
     *
     * @param reason an optional reason to be logged in the audit log.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete(reason: String? = null) {
        kord.rest.channel.deleteChannelPermission(channelId, data.id, reason)
    }

    /**
     * Returns a new [PermissionOverwriteEntity] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PermissionOverwriteEntity =
        PermissionOverwriteEntity(guildId, channelId, data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "PermissionOverwriteEntity(target=$target, type=$type, allowed=$allowed, denied=$denied, kord=$kord, supplier=$supplier)"
    }

}
