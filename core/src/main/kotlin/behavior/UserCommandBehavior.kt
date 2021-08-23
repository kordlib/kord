package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.application.*
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder
import dev.kord.rest.builder.interaction.ChatInputModifyBuilder
import dev.kord.rest.builder.interaction.MessageCommandModifyBuilder
import dev.kord.rest.builder.interaction.UserCommandModifyBuilder


interface UserCommandBehavior : ApplicationCommandBehavior {
    suspend fun edit(builder: suspend UserCommandModifyBuilder.() -> Unit): UserCommand

}


interface GlobalUserCommandBehavior : UserCommandBehavior, GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend UserCommandModifyBuilder.() -> Unit): GlobalUserCommand {
        val request = UserCommandModifyBuilder().apply { builder() }.toRequest()
        val response = service.modifyGlobalApplicationCommand(applicationId,id, request)
        val data = ApplicationCommandData.from(response)
        return GlobalUserCommand(data, service)
    }
}



interface GuildUserCommandBehavior : UserCommandBehavior, GuildApplicationCommandBehavior {
    override suspend fun edit(builder: suspend UserCommandModifyBuilder.() -> Unit): GuildUserCommand {
        val request = UserCommandModifyBuilder().apply { builder() }.toRequest()
        val response = service.modifyGuildApplicationCommand(applicationId, guildId, id, request)
        val data = ApplicationCommandData.from(response)
        return GuildUserCommand(data, service)
    }
}