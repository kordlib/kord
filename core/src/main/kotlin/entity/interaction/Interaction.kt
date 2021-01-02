package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Option
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.core.Kord
import dev.kord.core.behavior.InteractionBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.PartialInteractionBehavior
import dev.kord.core.cache.data.ApplicationCommandInteractionData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.data.OptionData
import dev.kord.core.entity.Entity
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

@KordPreview
class PartialInteraction(
    val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : PartialInteractionBehavior {

    override val id: Snowflake get() = data.id

    override val channelId: Snowflake get() = data.channelId

    override val token: String get() = data.token

    override val guildId: Snowflake get() = data.channelId


    val type: InteractionType get() = data.type

    val member: MemberBehavior get() = MemberBehavior(guildId, data.member.userId, kord)

    val version: Int get() = data.version

}

@KordPreview
class Interaction(
    val data: InteractionData,
    override val applicationId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) :
    InteractionBehavior {

    override val id: Snowflake get() = data.id

    override val channelId: Snowflake get() = data.channelId

    override val token: String get() = data.token

    override val guildId: Snowflake get() = data.channelId

    val type: InteractionType get() = data.type

    val member: MemberBehavior get() = MemberBehavior(guildId, data.member.userId, kord)

    val command: Command
        get() = Command(data.data)

    val version: Int get() = data.version

}

class Command(val data: ApplicationCommandInteractionData) : Entity {
    override val id: Snowflake
        get() = data.id

    val name get() = data.name

    //TODO: core Option
    val options get(): Map<String, Option> = data.options.orEmpty().associate { it.name to TODO() }

    //TODO: core Groups
    val groups get(): List<Any> = data.options.orEmpty()
            .filter { it.subCommand.orEmpty().isNotEmpty() }
            .map { TODO("make into group") }

    //TODO: core subcommands
    val subCommands get() = data.options.value?.map { it.subCommand }?.filterNot { it is Optional.Missing  }


}
