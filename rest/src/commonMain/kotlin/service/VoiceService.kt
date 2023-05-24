package dev.kord.rest.service

import dev.kord.common.entity.DiscordVoiceRegion
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*

public class VoiceService(public val client: HttpClient) {

    public suspend fun getVoiceRegions(): List<DiscordVoiceRegion> = client.get(Routes.Voice.Regions).body()
}
