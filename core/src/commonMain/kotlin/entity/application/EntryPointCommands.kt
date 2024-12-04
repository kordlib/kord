package dev.kord.core.entity.application

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.EntryPointCommandBehavior
import dev.kord.core.behavior.GlobalEntryPointCommandBehavior
import dev.kord.core.behavior.GuildEntryPointCommandBehavior
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.rest.service.InteractionService

public interface PrimaryEntryPointCommand : ApplicationCommand, EntryPointCommandBehavior

public class GuildPrimaryEntryPointCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService
) : PrimaryEntryPointCommand, GuildApplicationCommand, GuildEntryPointCommandBehavior {
    override val guildId: Snowflake
        get() = data.guildId.value!!
}

public class GlobalPrimaryEntryPointCommand(
    override val data: ApplicationCommandData,
    override val service: InteractionService
) : PrimaryEntryPointCommand, GlobalApplicationCommand, GlobalEntryPointCommandBehavior
