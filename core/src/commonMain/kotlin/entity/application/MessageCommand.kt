package dev.kord.core.entity.application

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GlobalMessageCommandBehavior
import dev.kord.core.behavior.GuildMessageCommandBehavior
import dev.kord.core.behavior.MessageCommandBehavior
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.rest.service.InteractionService

public sealed interface MessageCommand : ApplicationCommand, MessageCommandBehavior

public class GlobalMessageCommand(override val data: ApplicationCommandData, override val service: InteractionService) :
    GlobalApplicationCommand,
    MessageCommand,
    GlobalMessageCommandBehavior

public class GuildMessageCommand(override val data: ApplicationCommandData, override val service: InteractionService) :
    GuildApplicationCommand,
    MessageCommand,
    GuildMessageCommandBehavior {
    override val guildId: Snowflake
        get() = data.guildId.value!!
}
