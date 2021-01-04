package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.OptionValue
import dev.kord.common.entity.Permissions
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
import dev.kord.core.entity.Member
import dev.kord.core.supplier.EntitySupplier

/**
 * Interaction that can respond to interactions but can't follow-up due to the absence of [application id][Kord.resources].
 *
 * @property id interaction's id.
 * @property channelId the channel id where the interaction took place.
 * @property token a continuation token for responding to the interaction
 * @property guildId the id of the guild where the interaction took place.
 * @property permissions the permissions of the member with the overwrites.
 * @property type the type of the interaction.
 * @property member the invoker of the command as [MemberBehavior].
 * @property command [Command] object that contains the data related to the interaction's command.
 * @property version read-only property, always 1
 */
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

    val permissions: Permissions get() = data.permissions

    val member: MemberBehavior get() = MemberBehavior(guildId, data.member.userId, kord)

    val command: Command
        get() = Command(data.data)

    val version: Int get() = data.version

}

/**
 * Interaction that can respond to interactions and follow them up.
 *
 * @property id interaction's id.
 * @property channelId the channel id where the interaction took place.
 * @property token a continuation token for responding to the interaction
 * @property guildId the id of the guild where the interaction took place.
 * @property permissions the permissions of the member with the overwrites.
 * @property type the type of the interaction.
 * @property member the invoker of the command as [MemberBehavior].
 * @property command [Command] object that contains the data related to the interaction's command.
 * @property version read-only property, always 1
 */
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

    val permissions: Permissions get() = data.permissions

    val member: MemberBehavior get() = MemberBehavior(data.guildId, data.member.userId!!, kord)

    val command: Command
        get() = Command(data.data)

    val version: Int get() = data.version

}

/**
 * The root command in the interaction.
 *
 * @property name name of the command.
 * @property options  names of options in the command mapped to their values.
 * @property groups  names of groups in the command mapped to the [Group] with matching name.
 * @property subCommands  names of sub-commands in the command mapped to the [SubCommand] with matching name.
 */
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

/**
 * The Group containing [SubCommand]s  related to [Command].
 *
 *@property subCommands  names of sub-commands in this [Group] of commands mapped to the [SubCommand] with matching name.
 */
class Group(val data: OptionData) {
    val name: String get() = data.name

    val subCommands: Map<String, SubCommand>
        get() = data.subCommand.orEmpty()
            .associate { it.name to SubCommand(OptionData(it.name, values = it.options)) }

}

/**
 * A [SubCommand] that is either a part of [Command] or [Group].
 *
 * @property name name of the subcommand.
 * @property options  names of options in the command mapped to their values.
 */
class SubCommand(val data: OptionData) {
    val name: String get() = data.name
    val options: Map<String, OptionValue<*>>
        get() = data.values.orEmpty()
            .associate { it.name to it.value }
}



