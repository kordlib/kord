package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.Entity
import dev.kord.core.entity.interaction.ApplicationCommand
import dev.kord.core.entity.interaction.GlobalApplicationCommand
import dev.kord.core.entity.interaction.GuildApplicationCommand
import dev.kord.rest.builder.interaction.ApplicationCommandModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.InteractionService
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.Throws

@KordPreview
interface ApplicationCommandBehavior : Entity {
    val applicationId: Snowflake
    val service: InteractionService

    /**
     * Requests to edit this command, overwriting it with the data configured in [builder].
     * Returning the new version of this command.
     *
     * @throws [RestRequestException] when something goes wrong during the request.
     */
    suspend fun edit(
        builder: suspend /*suspend since not inline*/ ApplicationCommandModifyBuilder.() -> Unit)
    : ApplicationCommand

    /**
     * Requests to delete this command.
     *
     * @throws [RestRequestException] when something goes wrong during the request.
     */
    suspend fun delete()
}

@KordPreview
interface GlobalApplicationCommandBehavior : ApplicationCommandBehavior {

    override suspend fun edit(builder: suspend ApplicationCommandModifyBuilder.() -> Unit): GlobalApplicationCommand {
        val request = ApplicationCommandModifyBuilder().apply { builder() }.toRequest()
        val response = service.modifyGlobalApplicationCommand(applicationId, id, request)
        val data = ApplicationCommandData.from(response)
        return GlobalApplicationCommand(data, service)
    }

    override suspend fun delete() {
        service.deleteGlobalApplicationCommand(applicationId, id)
    }
}

@KordPreview
interface GuildApplicationCommandBehavior : ApplicationCommandBehavior {
    val guildId: Snowflake

    override suspend fun edit(builder: suspend ApplicationCommandModifyBuilder.() -> Unit): GuildApplicationCommand {
        val request = ApplicationCommandModifyBuilder().apply { builder() }.toRequest()
        val response = service.modifyGuildApplicationCommand(applicationId, guildId, id, request)
        val data = ApplicationCommandData.from(response)
        return GuildApplicationCommand(data, service, guildId)
    }

    override suspend fun delete() {
        service.deleteGuildApplicationCommand(applicationId, guildId, id)
    }
}
