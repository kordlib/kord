package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.OptionValue
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.InteractionBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.cache.data.ApplicationCommandInteractionData
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.cache.data.OptionData
import dev.kord.core.entity.Entity
import dev.kord.core.supplier.EntitySupplier

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
) : InteractionBehavior {

    override val id: Snowflake get() = data.id

    override val channelId: Snowflake get() = data.channelId

    override val token: String get() = data.token

    override val guildId: Snowflake get() = data.guildId

    val type: InteractionType get() = data.type

    val permissions: Permissions get() = data.permissions

    val channel: TextChannelBehavior get() = TextChannelBehavior(id = channelId, guildId = guildId, kord = kord)

    val guild get() = GuildBehavior(guildId, kord)

    val member: MemberBehavior get() = MemberBehavior(data.guildId, data.member.userId, kord)

    val command: Command
        get() = Command(data.data)

    val version: Int get() = data.version

}

/**
 * The root command in the interaction.
 *
 * @property name name of the command.
 * @property options  names of options in the command mapped to their values.
 * @property group  The group containing an invoked [SubCommand], if present.
 * @property subCommand The [SubCommand] invoked under this Command's name, if present.
 */
@KordPreview
class Command(val data: ApplicationCommandInteractionData) : Entity {
    override val id: Snowflake
        get() = data.id

    val name get() = data.name

    val options
        get(): Map<String, OptionValue<*>> = data.options.orEmpty()
            .filter { it.value !is Optional.Missing<*> }
            .associate { it.name to it.value.value!! }

    val group: Group?
        get() = data.options
            .firstOrNull { it.subCommands.orEmpty().isNotEmpty() }
            ?.let { Group(it) }


    val subCommand: SubCommand?
        get() = data.options
            .firstOrNull { it.values.orEmpty().isNotEmpty() }
            ?.let { SubCommand(it) }


}

/**
 * The Group containing [SubCommand] invoked from this Group.
 *
 *@property subCommand the [SubCommand] invoked from this Group.
 */
@KordPreview
class Group(val data: OptionData) {
    val name: String get() = data.name

    val subCommand: SubCommand
        get() = data.subCommands.unwrap {
            val command = it.first()
            SubCommand(OptionData(command.name, values = command.options))
        }!!

}

/**
 * A [SubCommand] that is either a part of [Command] or [Group].
 *
 * @property name name of the subcommand.
 * @property options  names of options in the command mapped to their values.
 */
@KordPreview
class SubCommand(val data: OptionData) {
    val name: String get() = data.name

    val options: Map<String, OptionValue<*>>
        get() = data.values.orEmpty()
            .associate { it.name to it.value }
}



