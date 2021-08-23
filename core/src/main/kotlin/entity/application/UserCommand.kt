package dev.kord.core.entity.application

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.*
import dev.kord.core.cache.data.ApplicationCommandData
import dev.kord.rest.service.InteractionService


sealed interface  UserCommand :  ApplicationCommand, UserCommandBehavior



class GlobalUserCommand(override val data: ApplicationCommandData, override val service: InteractionService) :
    GlobalApplicationCommand,
    UserCommand,
    GlobalUserCommandBehavior


class GuildUserCommand(override val data: ApplicationCommandData, override val service: InteractionService) :
    GuildApplicationCommand,
    UserCommand,
    GuildUserCommandBehavior {
    override val guildId: Snowflake
        get() = data.guildId.value!!
}