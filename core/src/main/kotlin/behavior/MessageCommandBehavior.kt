package dev.kord.core.behavior

import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.application.*
import dev.kord.rest.builder.interaction.MessageCommandModifyBuilder


public interface MessageCommandBehavior : ApplicationCommandBehavior {

    public suspend fun edit(builder: suspend MessageCommandModifyBuilder.() -> Unit): MessageCommand
}



public interface GuildMessageCommandBehavior : MessageCommandBehavior, GuildApplicationCommandBehavior {
    override suspend fun edit(builder: suspend MessageCommandModifyBuilder.() -> Unit): GuildMessageCommand {
        val request = MessageCommandModifyBuilder().apply { builder() }.toRequest()
        val response = service.modifyGuildApplicationCommand(applicationId, guildId, id, request)
        val data = ApplicationCommandData.from(response)
        return GuildMessageCommand(data, service)
    }
}



public interface GlobalMessageCommandBehavior : MessageCommandBehavior,GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend MessageCommandModifyBuilder.() -> Unit): GlobalMessageCommand {
        val request = MessageCommandModifyBuilder().apply { builder() }.toRequest()
        val response = service.modifyGlobalApplicationCommand(applicationId,id, request)
        val data = ApplicationCommandData.from(response)
        return GlobalMessageCommand(data, service)
    }
}
