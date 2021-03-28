package dev.kord.gateway.builder

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ActivityType
import dev.kord.common.entity.DiscordBotActivity
import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.optional.Optional
import dev.kord.gateway.DiscordPresence
import dev.kord.gateway.UpdateStatus
import java.time.Instant

@KordDsl
class PresenceBuilder {
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