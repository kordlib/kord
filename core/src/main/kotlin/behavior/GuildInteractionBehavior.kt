package dev.kord.core.behavior

import dev.kord.core.behavior.interaction.InteractionBehavior
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Guild

/**
 * The behavior of a [dev.kord.core.entity.interaction.Interaction][Interaction] that was invoked in a [Guild]
 */
public interface GuildInteractionBehavior : InteractionBehavior {

    public val guildId: Snowflake


    /**
     * The [GuildBehavior] for the guild the command was executed in.
     */
    public val guildBehavior: GuildBehavior get() = GuildBehavior(guildId, kord)


    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public companion object;

}
