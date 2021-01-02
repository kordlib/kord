package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.OptionValue
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.InteractionBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.PartialInteractionBehavior
import dev.kord.core.cache.data.ApplicationCommandInteractionData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.data.OptionData
import dev.kord.core.entity.Entity
import dev.kord.core.supplier.EntitySupplier

@KordPreview
class PartialInteraction(
    val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
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
    override val supplier: EntitySupplier = kord.defaultSupplier,
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

    val options
        get(): Map<String, OptionValue<*>> = data.options.orEmpty()
            .filter { it.value !is Optional.Missing<*> }
            .associate { it.name to it.value.value!! }

    val groups: Map<String, Group>
        get() = data.options.orEmpty()
            .filter { it.subCommand.orEmpty().isNotEmpty() }
            .associate { it.name to Group(it) }


    val subCommands: Map<String, SubCommand>
        get() = data.options.orEmpty()
            .filter { it.values.orEmpty().isNotEmpty() }
            .associate { it.name to SubCommand(it) }


}

class Group(val data: OptionData) {
    val name: String get() = data.name

    val subCommands: Map<String, SubCommand>
        get() = data.subCommand.orEmpty()
            .associate { it.name to SubCommand(OptionData(it.name, values = it.options)) }

}

class SubCommand(val data: OptionData) {
    val name: String get() = data.name
    val options: Map<String, OptionValue<*>>
        get() = data.values.orEmpty()
            .associate { it.name to it.value }
}



