package dev.kord.core.behavior

import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.application.ChatInputCommandCommand
import dev.kord.core.entity.application.GlobalChatInputCommand
import dev.kord.core.entity.application.GuildChatInputCommand
import dev.kord.rest.builder.interaction.ChatInputModifyBuilder


public interface ChatInputCommandBehavior : ApplicationCommandBehavior {

    public suspend fun edit(builder: suspend ChatInputModifyBuilder.() -> Unit): ChatInputCommandCommand

}


public interface GuildChatInputCommandBehavior : ChatInputCommandBehavior, GuildApplicationCommandBehavior {

    override suspend fun edit(builder: suspend ChatInputModifyBuilder.() -> Unit): GuildChatInputCommand {
        val response = service.modifyGuildChatInputApplicationCommand(applicationId, guildId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GuildChatInputCommand(data, service)
    }
}


public interface GlobalChatInputCommandBehavior : ChatInputCommandBehavior, GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend ChatInputModifyBuilder.() -> Unit): GlobalChatInputCommand {
        val response = service.modifyGlobalChatInputApplicationCommand(applicationId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GlobalChatInputCommand(data, service)
    }
}
