package dev.kord.core.behavior

import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.core.entity.application.PrimaryEntryPointCommand
import dev.kord.core.entity.application.GlobalPrimaryEntryPointCommand
import dev.kord.core.entity.application.GuildPrimaryEntryPointCommand
import dev.kord.rest.builder.interaction.EntryPointModifyBuilder

public interface EntryPointCommandBehavior : ApplicationCommandBehavior {
    public suspend fun edit(builder: suspend EntryPointModifyBuilder.() -> Unit): PrimaryEntryPointCommand
}

public interface GuildEntryPointCommandBehavior : EntryPointCommandBehavior, GuildApplicationCommandBehavior {
    override suspend fun edit(builder: suspend EntryPointModifyBuilder.() -> Unit): GuildPrimaryEntryPointCommand {
        val response = service.modifyGuildPrimaryEntryPointApplicationCommand(applicationId, guildId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GuildPrimaryEntryPointCommand(data, service)
    }
}

public interface GlobalEntryPointCommandBehavior : EntryPointCommandBehavior, GlobalApplicationCommandBehavior {
    override suspend fun edit(builder: suspend EntryPointModifyBuilder.() -> Unit): GlobalPrimaryEntryPointCommand {
        val response = service.modifyGlobalPrimaryEntryPointApplicationCommand(applicationId, id) {
            builder()
        }
        val data = ApplicationCommandData.from(response)
        return GlobalPrimaryEntryPointCommand(data, service)
    }
}
