package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull

public sealed interface ReactionEvent : Event, Strategizable {
    public val userId: Snowflake

    public val channelId: Snowflake

    public val messageId: Snowflake

    public val guildId: Snowflake?

    public val emoji: ReactionEmoji

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public val message: MessageBehavior get() = MessageBehavior(channelId, messageId, kord)

    public val user: UserBehavior get() = UserBehavior(userId, kord)

    public val userAsMember: MemberBehavior? get() = guildId?.let { MemberBehavior(it, userId, kord) }

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    public suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)

    public suspend fun getMessageOrNull(): Message? = supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    public suspend fun getUser(): User = supplier.getUser(userId)

    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    public suspend fun getUserAsMember(): Member? = guildId?.let { supplier.getMemberOrNull(it, userId) }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionEvent
}