package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.Entity
import dev.kord.core.entity.interaction.GlobalApplicationCommand
import dev.kord.core.entity.interaction.GuildApplicationCommand
import dev.kord.rest.builder.interaction.GlobalApplicationCommandModifyBuilder
import dev.kord.rest.builder.interaction.GuildApplicationCommandModifyBuilder
import dev.kord.rest.service.InteractionService

@KordPreview
interface GlobalApplicationCommandBehavior : Entity {
    val applicationId: Snowflake
    val service: InteractionService
    suspend fun edit(builder: GlobalApplicationCommandModifyBuilder.() -> Unit): GlobalApplicationCommand {
        val request = GlobalApplicationCommandModifyBuilder().apply(builder).toRequest()
        val response = service.modifyGlobalApplicationCommand(applicationId, id, request)
        val data = ApplicationCommandData.from(response)
        return GlobalApplicationCommand(data, service)
    }

    suspend fun delete() {
        service.deleteGlobalApplicationCommand(applicationId, id)
    }
}


@KordPreview
interface GuildApplicationCommandBehavior : Entity {
    val applicationId: Snowflake
    val guildId: Snowflake
    val service: InteractionService
    suspend fun edit(builder: GuildApplicationCommandModifyBuilder.() -> Unit): GuildApplicationCommand {
        val request = GuildApplicationCommandModifyBuilder().apply(builder).toRequest()
        val response = service.modifyGuildApplicationCommand(applicationId, guildId, id, request)
        val data = ApplicationCommandData.from(response)
        return GuildApplicationCommand(data, guildId, service)
    }

    suspend fun delete() {
        service.deleteGuildApplicationCommand(applicationId,guildId, id)
    }
}