package dev.kord.gateway.builder

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ActivityType
import dev.kord.common.entity.DiscordBotActivity
import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.orElse
import dev.kord.gateway.DiscordPresence
import dev.kord.gateway.UpdateStatus
import kotlinx.datetime.Instant

@KordDsl
public class PresenceBuilder {
    private var game: DiscordBotActivity? = null
    public var status: PresenceStatus = PresenceStatus.Online
    public var afk: Boolean = false
    public var since: Instant? = null
    private var _state: Optional<String?> = Optional.Missing()
    public var state: String? by ::_state.delegate()

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

    public fun toUpdateStatus(): UpdateStatus = UpdateStatus(since, listOfNotNull(game.withState(_state)), status, afk)

    public fun toPresence(): DiscordPresence = DiscordPresence(status, afk, since, game.withState(_state))
}

private fun DiscordBotActivity?.withState(state: Optional<String?>): DiscordBotActivity =
    this?.copy(state = this.state.orElse(state))
        ?: DiscordBotActivity(name = "Custom Status", state = state, type = ActivityType.Custom)
