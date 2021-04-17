package dev.kord.rest.service

import dev.kord.common.entity.DiscordDiscoveryCategory
import dev.kord.common.entity.DiscoveryTermValidationResponse
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class DiscoveryService(handler: RequestHandler) : RestService(handler) {
    suspend fun listDiscoveryCategories(): List<DiscordDiscoveryCategory> =
        call(Route.ListDiscoveryCategories)

    suspend fun validateTerm(term: String): DiscoveryTermValidationResponse = call(Route.ValidateDiscoverySearchTerm) {
        parameter("term", term)
    }
}
