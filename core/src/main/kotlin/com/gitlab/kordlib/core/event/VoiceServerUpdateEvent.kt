package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.common.entity.Snowflake

class VoiceServerUpdateEvent(
        val token: String,
        val guildId: Snowflake,
        val endpoint: String,
        override val kord: Kord,
        override val shard: Int
) : Event {

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild() : Guild = kord.getGuild(guildId)!!

}