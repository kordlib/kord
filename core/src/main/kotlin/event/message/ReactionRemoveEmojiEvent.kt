package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.cache.data.ReactionRemoveEmojiData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlin.coroutines.CoroutineContext

public class ReactionRemoveEmojiEvent(
    public val data: ReactionRemoveEmojiData,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : Event, Strategizable {

    /**
     * The id of the [TopGuildMessageChannel].
     */
    public val channelId: Snowflake get() = data.channelId

    public val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(
            guildId = guildId,
            id = channelId,
            kord = kord
        )

    /**
     * The id of the [Guild].
     */
    public val guildId: Snowflake get() = data.guildId

    public val guild: GuildBehavior get() = GuildBehavior(id = guildId, kord = kord)

    /**
     * The id of the message.
     */
    public val messageId: Snowflake get() = data.messageId

    public val message: MessageBehavior get() = MessageBehavior(channelId = channelId, messageId = messageId, kord = kord)

    /**
     * The emoji that was removed.
     */
    public val emoji: ReactionEmoji get() = ReactionEmoji.from(data.emoji)

    public suspend fun getChannel(): TopGuildMessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): TopGuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)

    public suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionRemoveAllEvent =
        ReactionRemoveAllEvent(channelId, messageId, guildId, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "ReactionRemoveEmojiEvent(data=$data, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
