package dev.kord.core.entity.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Message
import dev.kord.core.supplier.EntitySupplier
import java.util.*


/**
 * An [ApplicationCommandInteraction] that's invoked through messages.
 */
public sealed interface MessageCommandInteraction : ApplicationCommandInteraction {

    public val targetId: Snowflake get() = data.data.targetId.value!!

    public val targetBehavior: MessageBehavior get() = MessageBehavior(channelId, targetId, kord)

    public suspend fun getTarget(): Message = supplier.getMessage(channelId, targetId)

    public suspend fun getTargetOrNull(): Message? = supplier.getMessageOrNull(channelId, targetId)

    public val messages: Map<Snowflake, Message> get() = resolvedObjects!!.messages!!

}

/**
 * An [ApplicationCommandInteraction] that's invoked through messages specific to a guild.
 */
public class GuildMessageCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : MessageCommandInteraction, GuildApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GuildMessageCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id, guildId)
    }
}

/**
 * An [ApplicationCommandInteraction] that's invoked through messages.
 */
public class GlobalMessageCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : MessageCommandInteraction, GlobalApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GlobalMessageCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}

