package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

class ReactionRemoveEvent(
    val userId: Snowflake,
    val channelId: Snowflake,
    val messageId: Snowflake,
    override val guildId: Snowflake?,
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

    override fun toString(): String {
        return "ReactionRemoveEvent(userId=$userId, channelId=$channelId, messageId=$messageId, guildId=$guildId, emoji=$emoji, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
