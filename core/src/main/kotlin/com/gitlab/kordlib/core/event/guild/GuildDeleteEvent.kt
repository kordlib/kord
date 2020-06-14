package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.event.Event

class GuildDeleteEvent(
        val guildId: Snowflake,
        val unavailable: Boolean,
        val guild: Guild?,
        override val kord: Kord,
        override val shard: Int
) : Event
