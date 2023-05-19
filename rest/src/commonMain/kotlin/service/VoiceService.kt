package dev.kord.rest.service

import dev.kord.common.entity.DiscordVoiceRegion
import dev.kord.rest.route.Route
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.request.put

public class VoiceService(public val client: HttpClient) {

    public suspend fun getVoiceRegions(): List<DiscordVoiceRegion> = client.put(Routes.Voice.Regions).body()
}
