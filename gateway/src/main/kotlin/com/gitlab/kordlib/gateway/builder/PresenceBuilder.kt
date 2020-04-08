package com.gitlab.kordlib.gateway.builder

import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.DiscordActivity
import com.gitlab.kordlib.common.entity.ActivityType
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.gateway.Presence
import com.gitlab.kordlib.gateway.UpdateStatus
import java.time.Instant

@KordDsl
class PresenceBuilder  {
    private var game: DiscordActivity? = null
    var status: Status = Status.Online
    var afk: Boolean = false
    var since: Instant? = null

    fun playing(name: String) {
        game = DiscordActivity(name, ActivityType.Game)
    }

    fun listening(name: String) {
        game = DiscordActivity(name, ActivityType.Listening)
    }

    fun streaming(name: String, url: String) {
        game = DiscordActivity(name, ActivityType.Streaming, url = url)
    }

    fun watching(name: String) {
        game = DiscordActivity(name, ActivityType.Watching)
    }

    fun toUpdateStatus(): UpdateStatus = UpdateStatus(since?.toEpochMilli(), game, status, afk)

    fun toPresence(): Presence = Presence(status, afk, since?.toEpochMilli(), game)
}