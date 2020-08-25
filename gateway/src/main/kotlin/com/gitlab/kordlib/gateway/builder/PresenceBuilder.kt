package com.gitlab.kordlib.gateway.builder

import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.DiscordActivity
import com.gitlab.kordlib.common.entity.ActivityType
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.gateway.Presence
import com.gitlab.kordlib.gateway.UpdateStatus
import java.time.Instant

@KordDsl
class PresenceBuilder  {
    @OptIn(KordUnstableApi::class)
    private var game: DiscordActivity? = null
    var status: Status = Status.Online
    var afk: Boolean = false
    var since: Instant? = null

    fun playing(name: String) {
        @OptIn(KordUnstableApi::class)
        game = DiscordActivity(name, ActivityType.Game)
    }

    fun listening(name: String) {
        @OptIn(KordUnstableApi::class)
        game = DiscordActivity(name, ActivityType.Listening)
    }

    fun streaming(name: String, url: String) {
        @OptIn(KordUnstableApi::class)
        game = DiscordActivity(name, ActivityType.Streaming, url = url)
    }

    fun watching(name: String) {
        @OptIn(KordUnstableApi::class)
        game = DiscordActivity(name, ActivityType.Watching)
    }

    @OptIn(KordUnstableApi::class)
    fun toUpdateStatus(): UpdateStatus = UpdateStatus(since?.toEpochMilli(), game, status, afk)

    @OptIn(KordUnstableApi::class)
    fun toPresence(): Presence = Presence(status, afk, since?.toEpochMilli(), game)
}