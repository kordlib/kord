package dev.kord.core.event.automoderation

import dev.kord.common.entity.Permission.ManageGuild
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.behavior.automoderation.TypedAutoModerationRuleBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.automoderation.AutoModerationAction
import dev.kord.core.entity.automoderation.AutoModerationRule
import dev.kord.core.entity.automoderation.BlockMessageAutoModerationAction
import dev.kord.core.entity.automoderation.SendAlertMessageAutoModerationAction
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.automoderation.data.AutoModerationActionExecutionEventData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intent.MessageContent

/**
 * An [AutoModerationEvent] that is sent when an [AutoModerationRule] is triggered and an [AutoModerationAction] is
 * executed (e.g. when a message is [blocked][BlockMessageAutoModerationAction]).
 *
 * This event is only sent to bot users which have the [ManageGuild] permission.
 *
 * See [Auto moderation action execution event](https://discord.com/developers/docs/topics/gateway-events#auto-moderation-action-execution)
 */
public class AutoModerationActionExecutionEvent(
    public val data: AutoModerationActionExecutionEventData,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : AutoModerationEvent {

    /** The ID of the [Guild] in which the [action] was executed. */
    override val guildId: Snowflake get() = data.guildId

    /** The [AutoModerationAction] which was executed. */
    public val action: AutoModerationAction get() = AutoModerationAction(data.action, kord)

    /** The ID of the [AutoModerationRule] which the [action] belongs to. */
    override val ruleId: Snowflake get() = data.ruleId

    /** The behavior of the [AutoModerationRule] which the [action] belongs to. */
    override val rule: TypedAutoModerationRuleBehavior
        get() = TypedAutoModerationRuleBehavior(guildId, ruleId, data.ruleTriggerType, kord)

    /** The ID of the [Member] which generated the content which triggered the rule. */
    public val memberId: Snowflake get() = data.userId

    /** The behavior of the [Member] which generated the content which triggered the rule. */
    public val member: MemberBehavior get() = MemberBehavior(guildId, id = memberId, kord)

    /** The ID of the [GuildMessageChannel] in which user [content] was posted. */
    public val channelId: Snowflake? get() = data.channelId.value

    /** The behavior of the [GuildMessageChannel] in which user [content] was posted. */
    public val channel: GuildMessageChannelBehavior?
        get() = channelId?.let { GuildMessageChannelBehavior(guildId, id = it, kord) }

    /**
     * The ID of any user [Message] which [content] belongs to.
     *
     * This will be `null` if the message was [blocked][BlockMessageAutoModerationAction] by Auto Moderation or
     * [content] was not part of any message.
     */
    public val messageId: Snowflake? get() = data.messageId.value

    /**
     * The behavior of any user [Message] which [content] belongs to.
     *
     * This will be `null` if the message was [blocked][BlockMessageAutoModerationAction] by Auto Moderation or
     * [content] was not part of any message.
     */
    public val message: MessageBehavior?
        get() {
            return MessageBehavior(channelId ?: return null, messageId ?: return null, kord)
        }

    /**
     * The ID of any system Auto Moderation [Message] posted as a result of the [action].
     *
     * This will be `null` if the [action] is not of type [SendAlertMessageAutoModerationAction].
     */
    public val alertSystemMessageId: Snowflake? get() = data.alertSystemMessageId.value

    /**
     * The behavior of any system Auto Moderation [Message] posted as a result of the [action].
     *
     * This will be `null` if the [action] is not of type [SendAlertMessageAutoModerationAction].
     */
    public val alertSystemMessage: MessageBehavior?
        get() {
            val channelId = (action as? SendAlertMessageAutoModerationAction)?.channelId ?: return null
            return MessageBehavior(channelId, alertSystemMessageId ?: return null, kord)
        }

    /**
     * The user generated text content.
     *
     * The [MessageContent] intent is required to receive non-empty values.
     */
    public val content: String get() = data.content

    /**
     * The word or phrase configured in the [rule] that triggered it.
     *
     * This might be `null` if the [rule] was triggered for another reason.
     */
    public val matchedKeyword: String? get() = data.matchedKeyword

    /**
     * The substring in [content] that triggered the rule.
     *
     * This might be `null` for the same reasons as [matchedKeyword].
     *
     * The [MessageContent] intent is required to receive non-empty values.
     */
    public val matchedContent: String? get() = data.matchedContent

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationActionExecutionEvent =
        AutoModerationActionExecutionEvent(data, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String = "AutoModerationActionExecutionEvent(data=$data, kord=$kord, shard=$shard, " +
            "customContext=$customContext, supplier=$supplier)"
}
