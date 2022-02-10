package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.behavior.interaction.InteractionBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.ActionInteraction
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/**
 * The behavior of a [ActionInteraction] that was invoked in a [Guild]
 */
public interface GuildInteractionBehavior : InteractionBehavior {
    public val guildId: Snowflake

    /**
     * The [GuildBehavior] for the guild the command was executed in.
     */
    public val guildBehavior: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    override suspend fun getChannel(): GuildMessageChannel {
        return supplier.getChannelOf(channelId)
    }

    override suspend fun getChannelOrNull(): MessageChannel? {
        return supplier.getChannelOfOrNull(channelId)
    }

    public companion object

}

public fun GuildInteractionBehavior(
    guildId: Snowflake,
    id: Snowflake,
    channelId: Snowflake,
    applicationId: Snowflake,
    token: String,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
): GuildInteractionBehavior = object : GuildInteractionBehavior {
    override val guildId: Snowflake
        get() = guildId
    override val applicationId: Snowflake
        get() = applicationId
    override val token: String
        get() = token
    override val channelId: Snowflake
        get() = channelId
    override val kord: Kord
        get() = kord
    override val id: Snowflake
        get() = id
    override val supplier: EntitySupplier
        get() = supplier

}