package dev.kord.core.entity.interaction

import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.supplier.EntitySupplier
import java.util.*


/**
 * An [ApplicationCommandInteraction] that contains a [command].
 */
public sealed interface ChatInputCommandInteraction : ApplicationCommandInteraction {
    public val command: InteractionCommand get() = InteractionCommand(data.data, kord)
}



/**
 * A [ApplicationCommandInteraction] that's invoked through chat input specific to a guild.
 */
public class GuildChatInputCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ChatInputCommandInteraction, GuildApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GuildChatInputCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id, guildId)
    }
}


/**
 * An [ApplicationCommandInteraction] that's invoked through chat input.
 */
public class GlobalChatInputCommandInteraction(
    override val data: InteractionData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : ChatInputCommandInteraction, GlobalApplicationCommandInteraction {
    override fun equals(other: Any?): Boolean {
        return if (other !is GlobalChatInputCommandInteraction) false
        else id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}

