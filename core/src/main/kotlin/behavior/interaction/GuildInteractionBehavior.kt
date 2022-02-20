package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.GuildInteraction
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

/** The behavior of a [GuildInteraction]. */
public interface GuildInteractionBehavior : InteractionBehavior {

    /** The id of the guild the interaction was sent from. */
    public val guildId: Snowflake

    /** The behavior of the guild the interaction was sent from. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * The [GuildBehavior] for the guild the command was executed in.
     */
    @Deprecated("Renamed to 'guild'.", ReplaceWith("this.guild"), DeprecationLevel.ERROR)
    public val guildBehavior: GuildBehavior get() = guild

    override val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(guildId, channelId, kord)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    override suspend fun getChannel(): GuildMessageChannel = supplier.getChannelOf(channelId)

    override suspend fun getChannelOrNull(): GuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildInteractionBehavior =
        GuildInteractionBehavior(guildId, id, channelId, applicationId, token, kord, supplier)
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
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val channelId: Snowflake = channelId
    override val applicationId: Snowflake = applicationId
    override val token: String = token
    override val kord: Kord = kord
    override val supplier: EntitySupplier = supplier
}
