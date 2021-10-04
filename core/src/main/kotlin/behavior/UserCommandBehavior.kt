package dev.kord.core.behavior

import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.application.*
import dev.kord.rest.builder.interaction.UserCommandModifyBuilder


public interface UserCommandBehavior : ApplicationCommandBehavior {
    public suspend fun edit(builder: suspend UserCommandModifyBuilder.() -> Unit): UserCommand

}


public interface GlobalUserCommandBehavior : UserCommandBehavior, GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend UserCommandModifyBuilder.() -> Unit): GlobalUserCommand {
        val request = UserCommandModifyBuilder().apply { builder() }.toRequest()
        val response = service.modifyGlobalApplicationCommand(applicationId,id, request)
        val data = ApplicationCommandData.from(response)
        return GlobalUserCommand(data, service)
    }
}



public interface GuildUserCommandBehavior : UserCommandBehavior, GuildApplicationCommandBehavior {
    override suspend fun edit(builder: suspend UserCommandModifyBuilder.() -> Unit): GuildUserCommand {
        val request = UserCommandModifyBuilder().apply { builder() }.toRequest()
        val response = service.modifyGuildApplicationCommand(applicationId, guildId, id, request)
        val data = ApplicationCommandData.from(response)
        return GuildUserCommand(data, service)
    }
}
