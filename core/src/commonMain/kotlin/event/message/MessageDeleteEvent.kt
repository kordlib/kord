package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

public class MessageDeleteEvent(
    public val messageId: Snowflake,
    public val channelId: Snowflake,
    public val guildId: Snowflake?,
    public val message: Message?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    @Deprecated(
        "Deprecated in favour of getGuildOrNull() as it provides more clarity over the functionality",
        ReplaceWith("getGuildOrNull()"),
        DeprecationLevel.HIDDEN
    )
    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageDeleteEvent =
        MessageDeleteEvent(messageId, channelId, guildId, message, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MessageDeleteEvent(messageId=$messageId, channelId=$channelId, guildId=$guildId, message=$message, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
