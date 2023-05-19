package dev.kord.rest.service

import dev.kord.common.entity.DiscordGuild
import dev.kord.common.entity.DiscordTemplate
import dev.kord.common.entity.Snowflake
import dev.kord.rest.ById
import dev.kord.rest.Templates
import dev.kord.rest.builder.template.GuildFromTemplateCreateBuilder
import dev.kord.rest.builder.template.GuildTemplateCreateBuilder
import dev.kord.rest.builder.template.GuildTemplateModifyBuilder
import dev.kord.rest.json.request.GuildFromTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateCreateRequest
import dev.kord.rest.json.request.GuildTemplateModifyRequest
import dev.kord.rest.route.Routes
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class TemplateService(public val client: HttpClient) {

    /**
     * Returns a [DiscordTemplate] from the given code.
     */
    public suspend fun getGuildTemplate(code: String): DiscordTemplate =
        client.put(Routes.Guilds.Templates.ById(code)).body()

    /**
     * Create a new guild based on a template with the given [code] and configured by the [request], returns the created guild.
     */
    public suspend fun createGuildFromTemplate(code: String, request: GuildFromTemplateCreateRequest): DiscordGuild =
        client.post(Routes.Guilds.Templates.ById(code)) {
            setBody(request)
        }.body()

    /**
     * Creates a template given [guildId] configured by [request]
     *
     * Returns created [DiscordTemplate].
     */
    public suspend fun createGuildTemplate(guildId: Snowflake, request: GuildTemplateCreateRequest): DiscordTemplate =
        client.post(Routes.Guilds.ById.Templates(guildId)) {
            setBody(request)
        }.body()

    /**
     * Returns a list of  [DiscordTemplate] given [guildId].
     */
    public suspend fun getGuildTemplates(guildId: Snowflake): List<DiscordTemplate> =
        client.get(Routes.Guilds.ById.Templates(guildId)).body()

    /**
     * Synchronizes a template with [code] with the current state of the Guild with [guildId]
     *
     * Returns synchronized [DiscordTemplate].
     */
    public suspend fun syncGuildTemplate(guildId: Snowflake, code: String): DiscordTemplate =
        client.put(Routes.Guilds.ById.Templates.ById(guildId, code)).body()

    /**
     * Deletes a template given [code] and [guildId].
     *
     * Returns deleted [DiscordTemplate]
     */
    public suspend fun deleteGuildTemplate(guildId: Snowflake, code: String): DiscordTemplate =
        client.delete(Routes.Guilds.ById.Templates.ById(guildId, code)).body()

    /**
     * Modifies existing guild template configured by [request] given [code] and [guildId].
     *
     * Returns the modified [DiscordTemplate].
     */
    public suspend fun modifyGuildTemplate(
        guildId: Snowflake,
        code: String,
        request: GuildTemplateModifyRequest,
    ): DiscordTemplate =
        client.patch(Routes.Guilds.ById.Templates.ById(guildId, code)) {
        setBody(request)
    }.body()

    /**
     * Create a new guild with a [name] based on a template with the given [code] and configured by the [builder], returns the created guild.
     */
    public suspend inline fun createGuildFromTemplate(
        code: String,
        name: String,
        builder: GuildFromTemplateCreateBuilder.() -> Unit
    ): DiscordGuild {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = GuildFromTemplateCreateBuilder(name).apply(builder).toRequest()
        return createGuildFromTemplate(code, request)
    }

    /**
     * Modifies existing guild template configured by [builder] given [code] and [guildId].
     *
     * Returns the modified [DiscordTemplate].
     */
    public suspend inline fun modifyGuildTemplate(
        guildId: Snowflake,
        code: String,
        builder: GuildTemplateModifyBuilder.() -> Unit
    ): DiscordTemplate {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = GuildTemplateModifyBuilder().apply(builder).toRequest()
        return modifyGuildTemplate(guildId, code, request)
    }

    /**
     * Creates a guild template with [name] inside the guild with [guildId] configured by [builder].
     *
     * Returns the new [DiscordTemplate].
     */
    public suspend inline fun createGuildTemplate(
        guildId: Snowflake,
        name: String,
        builder: GuildTemplateCreateBuilder.() -> Unit
    ): DiscordTemplate {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        val request = GuildTemplateCreateBuilder(name).apply(builder).toRequest()
        return createGuildTemplate(guildId, request)
    }
}
