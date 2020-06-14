package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildMessageChannelBehavior
import com.gitlab.kordlib.core.cache.data.ReactionRemoveEmojiData
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.ReactionEmoji
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull

class ReactionRemoveEmojiEvent(
        val data: ReactionRemoveEmojiData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    /**
     * The id of the [GuildMessageChannel].
     */
    val channelId: Snowflake get() = Snowflake(data.channelId)

    val channel: GuildMessageChannelBehavior get() = GuildMessageChannelBehavior(guildId = guildId, id = channelId, kord = kord)

    /**
     * The id of the [Guild].
     */
    val guildId: Snowflake get() = Snowflake(data.guildId)

    val guild: GuildBehavior get() = GuildBehavior(id = guildId, kord = kord)

    /**
     * The id of the message.
     */
    val messageId: Snowflake get() = Snowflake(data.messageId)

    val message: MessageBehavior get() = MessageBehavior(channelId = channelId, messageId = messageId, kord = kord)

    /**
     * The emoji that was removed.
     */
    val emoji: ReactionEmoji get() = ReactionEmoji.from(data.emoji)

    suspend fun getChannel(): GuildMessageChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): GuildMessageChannel? = supplier.getChannelOfOrNull(channelId)

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)

    suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionRemoveAllEvent =
            ReactionRemoveAllEvent(channelId, messageId, guildId, kord, strategy.supply(kord))
}
