package dev.kord.core.event.message

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.entity.Member
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.User
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class ReactionAddEvent(
    override val userId: Snowflake,
    override val channelId: Snowflake,
    override val messageId: Snowflake,
    override val guildId: Snowflake?,
    override val emoji: ReactionEmoji,
    public val messageAuthorId: Snowflake?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : ReactionEvent {
    public val messageAuthor: UserBehavior? get() = messageAuthorId?.let { UserBehavior(it, kord) }
    public val messageAuthorAsMember: MemberBehavior?
        get() = guildId?.let { guildId ->
            messageAuthorId?.let { messageAuthorId ->
                MemberBehavior(guildId, messageAuthorId, kord)
            }
        }

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
