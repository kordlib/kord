package dev.kord.rest.service

import dev.kord.common.entity.DiscordApplication
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*

public class ApplicationService(public val client: HttpClient) {

    public suspend fun getCurrentApplicationInfo(): DiscordApplication = client.get(Routes.OAuth2.Applications.Me).body()
}
