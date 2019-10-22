package com.gitlab.kordlib.core.builder.presence

import com.gitlab.kordlib.common.entity.Activity
import com.gitlab.kordlib.common.entity.ActivityType
import com.gitlab.kordlib.common.entity.Status
import com.gitlab.kordlib.core.builder.KordDsl
import com.gitlab.kordlib.core.builder.RequestBuilder
import com.gitlab.kordlib.gateway.Presence
import com.gitlab.kordlib.gateway.UpdateStatus
import java.time.Instant

@KordDsl
class PresenceUpdateBuilder : RequestBuilder<UpdateStatus> {
    private var game: Activity? = null
    var status: Status = Status.Online
    var afk: Boolean = false
    var since: Instant? = null

    fun playing(name: String) {
        game = Activity(name, ActivityType.Game)
    }

    fun listening(name: String) {
        game = Activity(name, ActivityType.Listening)
    }

    fun streaming(name: String, url: String) {
        game = Activity(name, ActivityType.Streaming, url = url)
    }

    fun watching(name: String) {
        game = Activity(name, ActivityType.Watching)
    }

    override fun toRequest(): UpdateStatus = UpdateStatus(since?.toEpochMilli(), game, status, afk)

    fun toGatewayPresence(): Presence = Presence(status, afk, since?.toEpochMilli(), game)
}