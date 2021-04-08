package dev.kord.core.behavior

import behavior.interaction.InteractionBehavior
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Guild

interface GuildInteractionBehavior : InteractionBehavior {

    val guildId: Snowflake

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    companion object;

}