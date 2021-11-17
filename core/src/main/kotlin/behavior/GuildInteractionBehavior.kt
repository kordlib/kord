package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.MessageRespondingInteractionBehavior
import dev.kord.core.entity.Guild

/**
 * The behavior of a [dev.kord.core.entity.interaction.MessageRespondingInteraction][MessageRespondingInteraction] that was invoked in a [Guild]
 */
public interface GuildInteractionBehavior : MessageRespondingInteractionBehavior {

    public val guildId: Snowflake


    /**
     * The [GuildBehavior] for the guild the command was executed in.
     */
    public val guildBehavior: GuildBehavior get() = GuildBehavior(guildId, kord)


    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public companion object;

}
