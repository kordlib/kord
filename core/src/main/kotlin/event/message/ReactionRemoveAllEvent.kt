package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class ReactionRemoveAllEvent(
    public val channelId: Snowflake,
    public val messageId: Snowflake,
    public val guildId: Snowflake?,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord)
) : Event, CoroutineScope by coroutineScope, Strategizable {

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public val message: MessageBehavior get() = MessageBehavior(channelId, messageId, kord)

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    public suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)
    public suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionRemoveAllEvent =
        ReactionRemoveAllEvent(channelId, messageId, guildId, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "ReactionRemoveAllEvent(channelId=$channelId, messageId=$messageId, guildId=$guildId, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
