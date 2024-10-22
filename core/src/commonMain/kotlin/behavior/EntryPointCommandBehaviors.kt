package dev.kord.core.behavior

import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.application.EntryPointCommand
import dev.kord.core.entity.application.GlobalEntryPointCommand
import dev.kord.core.entity.application.GuildEntryPointCommand
import dev.kord.rest.builder.interaction.EntryPointModifyBuilder

public interface EntryPointCommandBehavior : ApplicationCommandBehavior {
    public suspend fun edit(builder: suspend EntryPointModifyBuilder.() -> Unit): EntryPointCommand
}

public interface GuildEntryPointCommandBehavior : EntryPointCommandBehavior, GuildApplicationCommandBehavior {
    override suspend fun edit(builder: suspend EntryPointModifyBuilder.() -> Unit): GuildEntryPointCommand {
        val response = service.modifyGuildEntryPointApplicationCommand(applicationId, guildId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GuildEntryPointCommand(data, service)
    }
}

public interface GlobalEntryPointCommandBehavior : EntryPointCommandBehavior, GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend EntryPointModifyBuilder.() -> Unit): GlobalEntryPointCommand {
        val response = service.modifyGlobalEntryPointApplicationCommand(applicationId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GlobalEntryPointCommand(data, service)
    }
}
