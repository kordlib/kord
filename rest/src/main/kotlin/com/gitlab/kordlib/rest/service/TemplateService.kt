package dev.kord.rest.service

import dev.kord.common.entity.DiscordGuild
import dev.kord.common.entity.DiscordTemplate
import dev.kord.rest.builder.template.GuildFromTemplateCreateBuilder
import dev.kord.rest.json.request.GuildTemplateCreateRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class TemplateService(requestHandler: RequestHandler) : RestService(requestHandler) {

    /**
     * Returns a [DiscordTemplate] from the given code.
     */
    suspend fun getTemplate(code: String): DiscordTemplate = call(Route.TemplateGet){
        keys[Route.TemplateCode] = code
    }

    /**
     * Create a new guild based on a template with the given [code] and configured by the [request], returns the created guild.
     */
    suspend fun createGuildTemplate(code: String, request: GuildTemplateCreateRequest): DiscordGuild = call(Route.TemplatePost){
        keys[Route.TemplateCode] = code
        body(GuildTemplateCreateRequest.serializer(), request)
    }

    /**
     * Create a new guild with a [name] based on a template with the given [code] and configured by the [builder], returns the created guild.
     */
    suspend inline fun createGuildTemplate(code: String, name: String, builder: GuildFromTemplateCreateBuilder.() -> Unit): DiscordGuild {
        val request = GuildFromTemplateCreateBuilder(name).apply(builder).toRequest()
        return createGuildTemplate(code, request)
    }

}