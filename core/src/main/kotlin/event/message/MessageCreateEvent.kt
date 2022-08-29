package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class MessageCreateEvent(
    public val message: Message,
    public val guildId: Snowflake?,
    public val member: Member?,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = message.kord.defaultSupplier,
) : Event, Strategizable {
    override val kord: Kord get() = message.kord

    /**
     * Requests to get the guild this message was created in, if it was created in one,
     * returns null if the [Guild] isn't present or the message was a [DM][DmChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageCreateEvent =
        MessageCreateEvent(message, guildId, member, shard, customContext, strategy.supply(message.kord))

    override fun toString(): String {
        return "MessageCreateEvent(message=$message, guildId=$guildId, member=$member, shard=$shard, supplier=$supplier)"
    }
}
