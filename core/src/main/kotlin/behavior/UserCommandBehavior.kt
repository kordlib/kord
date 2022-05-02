package dev.kord.core.behavior

import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.application.GlobalUserCommand
import dev.kord.core.entity.application.GuildUserCommand
import dev.kord.core.entity.application.UserCommand
import dev.kord.rest.builder.interaction.UserCommandModifyBuilder


public interface UserCommandBehavior : ApplicationCommandBehavior {
    public suspend fun edit(builder: suspend UserCommandModifyBuilder.() -> Unit): UserCommand

}


public interface GlobalUserCommandBehavior : UserCommandBehavior, GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend UserCommandModifyBuilder.() -> Unit): GlobalUserCommand {
        val response = service.modifyGlobalUserApplicationCommand(applicationId, id) { builder() }
        val data = ApplicationCommandData.from(response)
        return GlobalUserCommand(data, service)
    }
}


public interface GuildUserCommandBehavior : UserCommandBehavior, GuildApplicationCommandBehavior {
    override suspend fun edit(builder: suspend UserCommandModifyBuilder.() -> Unit): GuildUserCommand {
        val response = service.modifyGuildUserApplicationCommand(applicationId, guildId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GuildUserCommand(data, service)
    }
}
