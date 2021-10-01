package dev.kord.core.entity.channel

import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.value
import dev.kord.common.exception.RequestException
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.TopGuildChannelBehavior
import dev.kord.core.cache.data.PermissionOverwriteData
import dev.kord.core.entity.PermissionOverwriteEntity
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of a Discord channel associated to a [guild].
 */
public interface GuildChannel : Channel, GuildChannelBehavior {

    override val guildId: Snowflake
        get() = data.guildId.value!!

    /**
     * The name of this channel.
     */
    public val name: String get() = data.name.value!!

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildChannel

}
