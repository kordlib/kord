package dev.kord.rest.service

import dev.kord.common.entity.DiscordGuild
import dev.kord.common.entity.DiscordTemplate
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.template.GuildFromTemplateCreateBuilder
import dev.kord.rest.builder.template.GuildTemplateCreateBuilder
import dev.kord.rest.builder.template.GuildTemplateModifyBuilder
import dev.kord.rest.json.request.GuildFromTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateModifyRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route

class TemplateService(requestHandler: RequestHandler) : RestService(requestHandler) {

    /**
     * Returns a [DiscordTemplate] from the given code.
     */
    suspend fun getGuildTemplate(code: String): DiscordTemplate = call(Route.TemplateGet) {
        keys[Route.TemplateCode] = code
    }

    /**
     * Create a new guild based on a template with the given [code] and configured by the [request], returns the created guild.
     */
    suspend fun createGuildFromTemplate(code: String, request: GuildFromTemplateCreateRequest): DiscordGuild = call(Route.GuildFromTemplatePost) {
        keys[Route.TemplateCode] = code
        body(GuildFromTemplateCreateRequest.serializer(), request)
    }

    suspend fun createGuildTemplate(guildId: Snowflake, request: GuildTemplateCreateRequest) = call(Route.GuildTemplatePost) {
        keys[Route.GuildId] = guildId
        body(GuildTemplateCreateRequest.serializer(), request)
    }

    suspend fun getGuildTemplates(guildId: Snowflake) = call(Route.GuildTemplatesGet) {
        keys[Route.GuildId] = guildId
    }

    suspend fun syncGuildTemplate(guildId: Snowflake, code: String) = call(Route.TemplateSyncPut) {
        keys[Route.GuildId] = guildId
        keys[Route.TemplateCode] = code
    }

    suspend fun deleteGuildTemplate(guildId: Snowflake, code: String) = call(Route.TemplateDelete) {
        keys[Route.GuildId] = guildId
        keys[Route.TemplateCode] = code
    }

    suspend fun modifyGuildTemplate(guildId: Snowflake, code: String, request: GuildTemplateModifyRequest) = call(Route.TemplatePatch) {
        keys[Route.GuildId] = guildId
        keys[Route.TemplateCode] = code
        body(GuildTemplateModifyRequest.serializer(), request)
    }


    /**
     * Create a new guild with a [name] based on a template with the given [code] and configured by the [builder], returns the created guild.
     */
    suspend inline fun createGuildFromTemplate(code: String, name: String, builder: GuildFromTemplateCreateBuilder.() -> Unit): DiscordGuild {
        val request = GuildFromTemplateCreateBuilder(name).apply(builder).toRequest()
        return createGuildFromTemplate(code, request)
    }

    suspend inline fun modifyGuildTemplate(guildId: Snowflake, code: String, builder: GuildTemplateModifyBuilder.() -> Unit): DiscordTemplate {
        val request = GuildTemplateModifyBuilder().apply(builder).toRequest()
        return modifyGuildTemplate(guildId, code, request)
    }

    suspend inline fun createGuildTemplate(guildId: Snowflake, name: String, builder: GuildTemplateCreateBuilder.() -> Unit): DiscordTemplate {
        val request = GuildTemplateCreateBuilder(name).apply(builder).toRequest()
        return createGuildTemplate(guildId, request)
    }

}