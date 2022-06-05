package dev.kord.gateway.builder

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ActivityType
import dev.kord.common.entity.DiscordBotActivity
import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.optional.Optional
import dev.kord.gateway.DiscordPresence
import dev.kord.gateway.UpdateStatus
import kotlinx.datetime.Instant

@KordDsl
public class PresenceBuilder {
    private var game: DiscordBotActivity? = null
    public var status: PresenceStatus = PresenceStatus.Online
    public var afk: Boolean = false
    public var since: Instant? = null

    public fun playing(name: String) {
        game = DiscordBotActivity(name, ActivityType.Game)
    }

    public fun listening(name: String) {
        game = DiscordBotActivity(name, ActivityType.Listening)
    }

    public fun streaming(name: String, url: String) {
        game = DiscordBotActivity(name, ActivityType.Streaming, url = Optional(url))
    }

    public fun watching(name: String) {
        game = DiscordBotActivity(name, ActivityType.Watching)
    }

    public fun competing(name: String) {
        game = DiscordBotActivity(name, ActivityType.Competing)
    }

    public fun toUpdateStatus(): UpdateStatus = UpdateStatus(since, game?.let(::listOf).orEmpty(), status, afk)

    public fun toPresence(): DiscordPresence = DiscordPresence(status, afk, since, game)
}
