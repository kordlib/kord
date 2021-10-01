package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.TopGuildChannelBehavior
import dev.kord.core.cache.data.PermissionOverwriteData
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.request.RestRequestException

public class PermissionOverwriteEntity(
    public val guildId: Snowflake,
    public val channelId: Snowflake,
    data: PermissionOverwriteData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : PermissionOverwrite(data), KordObject, Strategizable {

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public val channel: TopGuildChannelBehavior get() = TopGuildChannelBehavior(guildId, channelId, kord)

    /**
     * Requests to get the channel this overwrite applies to.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [TopGuildChannel] wasn't present.
     */
    public suspend fun getChannel(): TopGuildChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel this overwrite applies to,
     * returns null if the [TopGuildChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): TopGuildChannel? = supplier.getChannelOfOrNull(channelId)

    /**
     * Requests to get the the guild of this overwrite.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild of this overwrite,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to delete this overwrite.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete(reason: String? = null) {
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
