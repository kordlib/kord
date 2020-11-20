package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.entity.channel.DmChannel

class MessageCreateEvent(
        val message: Message,
        val guildId: Snowflake?,
        val member: Member?,
        override val shard: Int,
        override val supplier: EntitySupplier = message.kord.defaultSupplier
) : Event, Strategizable {
    override val kord: Kord get() = message.kord

    /**
     * Requests to get the guild this message was created in, if it was created in one,
     * returns null if the [Guild] isn't present or the message was a [DM][DmChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
            MessageCreateEvent(message, guildId, member, shard, strategy.supply(message.kord))

    override fun toString(): String {
        return "MessageCreateEvent(message=$message, guildId=$guildId, member=$member, shard=$shard, supplier=$supplier)"
    }
}
