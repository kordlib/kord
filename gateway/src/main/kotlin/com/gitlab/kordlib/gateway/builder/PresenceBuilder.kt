package com.gitlab.kordlib.gateway.builder

import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.ActivityType
import com.gitlab.kordlib.common.entity.PresenceStatus
import com.gitlab.kordlib.common.entity.DiscordBotActivity
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.gateway.DiscordPresence
import com.gitlab.kordlib.gateway.UpdateStatus
import java.time.Instant

@KordDsl
class PresenceBuilder  {
    private var game: DiscordBotActivity? = null
    var status: PresenceStatus = PresenceStatus.Online
    var afk: Boolean = false
    var since: Instant? = null

    fun playing(name: String) {
        game = DiscordBotActivity(name, ActivityType.Game)
    }

    fun listening(name: String) {
        game = DiscordBotActivity(name, ActivityType.Listening)
    }

    fun streaming(name: String, url: String) {
        game = DiscordBotActivity(name, ActivityType.Streaming, url = Optional(url))
    }

    fun watching(name: String) {
        game = DiscordBotActivity(name, ActivityType.Watching)
    }

    fun toUpdateStatus(): UpdateStatus = UpdateStatus(since?.toEpochMilli(), game?.let(::listOf), status, afk)

    fun toPresence(): DiscordPresence = DiscordPresence(status, afk, since?.toEpochMilli(), game)
}