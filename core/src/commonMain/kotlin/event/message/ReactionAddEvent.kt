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

public class ReactionAddEvent(
    public val userId: Snowflake,
    public val channelId: Snowflake,
    public val messageId: Snowflake,
    public val guildId: Snowflake?,
    public val emoji: ReactionEmoji,
    public val messageAuthorId: Snowflake?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public val message: MessageBehavior get() = MessageBehavior(channelId, messageId, kord)

    public val user: UserBehavior get() = UserBehavior(userId, kord)
    public val userAsMember: MemberBehavior? get() = guildId?.let { MemberBehavior(it, userId, kord) }

    public val messageAuthor: UserBehavior? get() = messageAuthorId?.let { UserBehavior(it, kord) }
    public val messageAuthorAsMember: MemberBehavior?
        get() = guildId?.let { guildId ->
            messageAuthorId?.let { messageAuthorId ->
                MemberBehavior(guildId, messageAuthorId, kord)
            }
        }

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    public suspend fun getMessage(): Message = supplier.getMessage(channelId = channelId, messageId = messageId)
    public suspend fun getMessageOrNull(): Message? =
        supplier.getMessageOrNull(channelId = channelId, messageId = messageId)

    public suspend fun getUser(): User = supplier.getUser(userId)
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)
    public suspend fun getUserAsMember(): Member? = guildId?.let { supplier.getMemberOrNull(it, userId) }

    public suspend fun getMessageAuthorOrNull(): User? = messageAuthorId?.let { supplier.getUserOrNull(it) }
    public suspend fun getMessageAuthorAsMemberOrNull(): Member? = guildId?.let { guildId ->
        messageAuthorId?.let { messageAuthorId ->
            supplier.getMemberOrNull(guildId, messageAuthorId)
        }
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ReactionAddEvent = ReactionAddEvent(
        userId,
        channelId,
        messageId,
        guildId,
        emoji,
        messageAuthorId,
        kord,
        shard,
        customContext,
        strategy.supply(kord)
    )

    override fun toString(): String = "ReactionAddEvent(userId=$userId, channelId=$channelId, messageId=$messageId, " +
        "guildId=$guildId, emoji=$emoji, messageAuthorId=$messageAuthorId, kord=$kord, shard=$shard, " +
        "customContext=$customContext, supplier=$supplier)"
}
