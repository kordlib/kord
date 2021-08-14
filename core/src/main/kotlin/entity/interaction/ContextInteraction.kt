package dev.kord.core.entity.interaction

import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.interaction.ApplicationCommandInteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

sealed interface ApplicationCommandInteraction : Interaction, ApplicationCommandInteractionBehavior

sealed interface  ChatInputCommandInteraction : ApplicationCommandInteraction {
    val command: InteractionCommand get() =  InteractionCommand(data.data, kord)


}

class GuildChatInputCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ChatInputCommandInteraction, GuildApplicationCommandInteraction

class GlobalChatInputCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ChatInputCommandInteraction, GlobalApplicationCommandInteraction


sealed interface  UserCommandInteraction : ApplicationCommandInteraction {
    private val resolvedUsersData  get() = data.data.resolvedObjectsData.value?.users?.value
    val users get() = resolvedUsersData.orEmpty().mapValues { User(it.value, kord) }
}

class GuildUserCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : UserCommandInteraction, GuildApplicationCommandInteraction {
    private val resolvedMembersData  get() = data.data?.resolvedObjectsData.value?.members?.value
    val members get() = resolvedMembersData.orEmpty().mapValues { memberData ->
        Member(memberData.value, users[memberData.key]!!.data, kord) }
}

class GlobalUserCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : UserCommandInteraction, GlobalApplicationCommandInteraction


sealed interface  MessageCommandInteraction : ApplicationCommandInteraction {
    private val resolvedMessagesData get() = data.data.resolvedObjectsData.value?.messages?.value
    val messages get() = resolvedMessagesData.orEmpty().mapValues { Message(it.value, kord) }

}

class GuildMessageCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : MessageCommandInteraction, GuildApplicationCommandInteraction

class GlobalMessageCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : MessageCommandInteraction, GlobalApplicationCommandInteraction

class UnknownApplicationCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : MessageCommandInteraction, ApplicationCommandInteraction {
    override val user: UserBehavior
        get() = UserBehavior(data.user.value!!.id, kord)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Interaction {
        TODO("Not yet implemented")
    }
}
