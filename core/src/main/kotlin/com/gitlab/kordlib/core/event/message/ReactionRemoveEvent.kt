package com.gitlab.kordlib.core.event.message

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull

class ReactionRemoveEvent(
        val userId: Snowflake,
        val channelId: Snowflake,
        val messageId: Snowflake,
        val guildId: Snowflake?,
        val emoji: ReactionEmoji,
        override val kord: Kord,
        override val shard: Int,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    val message: MessageBehavior get() = MessageBehavior(channelId, messageId, kord)

    val user: UserBehavior get() = UserBehavior(userId, kord)

    val userAsMember: MemberBehavior? get() = guildId?.let { MemberBehavior(it, userId, kord) }

    suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)

    suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    suspend fun getUser(): User = supplier.getUser(userId)

    suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    suspend fun getUserAsMember(): Member? =
            guildId?.let { supplier.getMemberOrNull(guildId = guildId, userId = userId) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionRemoveEvent =
            ReactionRemoveEvent(userId, channelId, messageId, guildId, emoji, kord, shard, strategy.supply(kord))
}
