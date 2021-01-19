package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.Entity
import dev.kord.core.entity.interaction.ApplicationCommand
import dev.kord.core.entity.interaction.GlobalApplicationCommand
import dev.kord.rest.builder.interaction.ApplicationCommandModifyBuilder
import dev.kord.rest.service.InteractionService

@KordPreview
interface ApplicationCommandBehavior : Entity {
    val applicationId: Snowflake
    val service: InteractionService

    suspend fun delete()
}

@KordPreview
suspend inline fun ApplicationCommandBehavior.edit(builder: ApplicationCommandModifyBuilder.() -> Unit): ApplicationCommand {
    val request = ApplicationCommandModifyBuilder().apply(builder).toRequest()
    val response = when (this) { //sealed classes save us
        is GlobalApplicationCommandBehavior ->
            service.modifyGlobalApplicationCommand(applicationId, id, request)
        is GuildApplicationCommandBehavior ->
            service.modifyGuildApplicationCommand(applicationId, commandId = id, guildId = guildId, request = request)
        else -> error("unexpected receiver type $this")
    }
    val data = ApplicationCommandData.from(response)
    return GlobalApplicationCommand(data, service)
}

@KordPreview
interface GlobalApplicationCommandBehavior : ApplicationCommandBehavior {

    override suspend fun delete() {
        service.deleteGlobalApplicationCommand(applicationId, id)
    }
}

@KordPreview
interface GuildApplicationCommandBehavior : ApplicationCommandBehavior {
    val guildId: Snowflake

    override suspend fun delete() {
        service.deleteGuildApplicationCommand(applicationId, guildId, id)
    }
}
