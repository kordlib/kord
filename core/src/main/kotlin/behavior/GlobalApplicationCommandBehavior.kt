package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Entity
import dev.kord.rest.builder.interaction.ApplicationCommandPermissionsModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.InteractionService

/**
 * The behavior of [Application Command][dev.kord.core.entity.application.ApplicationCommand].
 */
interface ApplicationCommandBehavior : Entity {
    val applicationId: Snowflake
    val service: InteractionService

    /**
     * Requests to delete this command.
     *
     * @throws [RestRequestException] when something goes wrong during the request.
     */
    suspend fun delete()

}


/**
 * The behavior of [dev.kord.core.entity.application.ApplicationCommand].
 */
interface GlobalApplicationCommandBehavior : ApplicationCommandBehavior {

    override suspend fun delete() {
        service.deleteGlobalApplicationCommand(applicationId, id)
    }

    /**
     * Updates the permissions for this command on the guild corresponding to [guildId].
     *
     * @throws [RestRequestException] when something goes wrong during the request.
     */
    suspend fun editPermissions(
        guildId: Snowflake,
        builder: ApplicationCommandPermissionsModifyBuilder.() -> Unit
    ) {
        val request = ApplicationCommandPermissionsModifyBuilder().apply(builder).toRequest()
        service.editApplicationCommandPermissions(applicationId, guildId, id, request)
    }
}


/**
 * The behavior of [Application Command][dev.kord.core.entity.application.GuildApplicationCommand].
 */
interface GuildApplicationCommandBehavior : ApplicationCommandBehavior {
    val guildId: Snowflake

    /**
     * Updates the permissions for this command on the guild.
     *
     * @throws [RestRequestException] when something goes wrong during the request.
     */
    suspend fun editPermissions(
        builder: ApplicationCommandPermissionsModifyBuilder.() -> Unit
    ) {
        val request = ApplicationCommandPermissionsModifyBuilder().apply(builder).toRequest()
        service.editApplicationCommandPermissions(applicationId, guildId, id, request)
    }

    override suspend fun delete() {
        service.deleteGuildApplicationCommand(applicationId, guildId, id)
    }

}


fun GuildApplicationCommandBehavior(
    guildId: Snowflake,
    applicationId: Snowflake,
    id: Snowflake,
    service: InteractionService
): GuildApplicationCommandBehavior = object : GuildApplicationCommandBehavior {
    override val guildId: Snowflake
        get() = guildId
    override val applicationId: Snowflake
        get() = applicationId
    override val service: InteractionService
        get() = service
    override val id: Snowflake
        get() = id
}


fun GlobalApplicationCommandBehavior(
    applicationId: Snowflake,
    id: Snowflake,
    service: InteractionService
): GlobalApplicationCommandBehavior = object : GlobalApplicationCommandBehavior {
    override val applicationId: Snowflake
        get() = applicationId
    override val service: InteractionService
        get() = service
    override val id: Snowflake
        get() = id
}
