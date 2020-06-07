package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.GuildEmoji
import com.gitlab.kordlib.core.event.Event

class EmojisUpdateEvent internal constructor(val guildId: Snowflake, val emojis: Set<GuildEmoji>, override val kord: Kord) : Event {

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

}