package dev.kord.rest.json.response

import dev.kord.common.entity.DiscordSoundboardSound
import kotlinx.serialization.Serializable

@Serializable
public data class GuildSoundboardSoundsResponse(val items: List<DiscordSoundboardSound>)