package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.common.entity.DiscordPresenceUser
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Presence
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.User

class PresenceUpdateEvent internal constructor(
        val oldUser: User?,
        val user: DiscordPresenceUser,
        val guildId: Snowflake,
        val old: Presence?,
        val presence: Presence
) : Event {
    override val kord: Kord get() = presence.kord
}