package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.OptionValue
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.core.Kord
import dev.kord.core.KordObject
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

@KordPreview
sealed class Command {

    abstract val rootId: Snowflake
    abstract val name: String
    abstract val options: Map<String, OptionValue<*>>

    companion object {
        operator fun invoke(data: ApplicationCommandInteractionData): Command {
            val firstLevelOptions = data.options.orEmpty()
            val rootPredicate = firstLevelOptions.isEmpty() || firstLevelOptions.any { it.value.value != null }
            val groupPredicate = firstLevelOptions.any { it.subCommands.orEmpty().isNotEmpty() }
            val subCommandPredicate = firstLevelOptions.any { it.values.orEmpty().isNotEmpty() }

            return when {
                rootPredicate -> RootCommand(data)
                groupPredicate -> GroupCommand(data)
                subCommandPredicate -> SubCommand(data)
                else -> error("Undefined data structure")
            }
        }
    }
}

@KordPreview
class RootCommand(val data: ApplicationCommandInteractionData) : Command() {

     override val rootId: Snowflake
        get() = data.id

    override val name get() = data.name

    override val options: Map<String, OptionValue<*>>
        get() = data.options.orEmpty()
            .associate { it.name to it.value.value!! }

}

@KordPreview
class SubCommand(val data: ApplicationCommandInteractionData) : Command() {

    private val subCommandData = data.options.orEmpty().first()

    val rootName get() = data.name

    override val rootId: Snowflake
        get() = data.id

    override val name get() = subCommandData.name

    override val options: Map<String, OptionValue<*>>
        get() = subCommandData.values.orEmpty()
            .associate { it.name to it.value }

}

@KordPreview
class GroupCommand(val data: ApplicationCommandInteractionData) : Command() {

    private val groupData get() = data.options.orEmpty().first()
    private val subCommandData get() = groupData.subCommands.orEmpty().first()

    override val rootId: Snowflake
        get() = data.id

    val rootName get() = data.name

    val groupName get() = groupData.name

    override val name get() = subCommandData.name

    override val options: Map<String, OptionValue<*>>
        get() = subCommandData.options.orEmpty()
            .associate { it.name to it.value }
}



