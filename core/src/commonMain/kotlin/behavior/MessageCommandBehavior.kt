package dev.kord.core.behavior

import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.application.GlobalMessageCommand
import dev.kord.core.entity.application.GuildMessageCommand
import dev.kord.core.entity.application.MessageCommand
import dev.kord.rest.builder.interaction.MessageCommandModifyBuilder


public interface MessageCommandBehavior : ApplicationCommandBehavior {

    public suspend fun edit(builder: suspend MessageCommandModifyBuilder.() -> Unit): MessageCommand
}


public interface GuildMessageCommandBehavior : MessageCommandBehavior, GuildApplicationCommandBehavior {
    override suspend fun edit(builder: suspend MessageCommandModifyBuilder.() -> Unit): GuildMessageCommand {
        val response = service.modifyGuildMessageApplicationCommand(applicationId, guildId, id) { builder() }
        val data = ApplicationCommandData.from(response)
        return GuildMessageCommand(data, service)
    }
}


public interface GlobalMessageCommandBehavior : MessageCommandBehavior, GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend MessageCommandModifyBuilder.() -> Unit): GlobalMessageCommand {
        val response = service.modifyGlobalMessageApplicationCommand(applicationId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GlobalMessageCommand(data, service)
    }
}
