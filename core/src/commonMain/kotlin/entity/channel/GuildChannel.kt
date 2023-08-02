package dev.kord.core.entity.channel

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.GuildChannelBehavior
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
