package dev.kord.core.event.automoderation.data

import dev.kord.common.entity.AutoModerationRuleTriggerType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.core.cache.data.AutoModerationActionData
import dev.kord.gateway.DiscordAutoModerationActionExecution
import kotlinx.serialization.Serializable

/**
 * The data for the event dispatched when an auto-moderation action is executed.
 *
 * See [Auto moderation action execution](https://discord.com/developers/docs/topics/gateway-events#auto-moderation-action-execution)
 */
@Serializable
public data class AutoModerationActionExecutionEventData(
    val guildId: Snowflake,
    val action: AutoModerationActionData,
    val ruleId: Snowflake,
    val ruleTriggerType: AutoModerationRuleTriggerType,
    val userId: Snowflake,
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    val messageId: OptionalSnowflake = OptionalSnowflake.Missing,
    val alertSystemMessageId: OptionalSnowflake = OptionalSnowflake.Missing,
    val content: String,
    val matchedKeyword: String?,
    val matchedContent: String?,
) {
    public companion object {
        public fun from(entity: DiscordAutoModerationActionExecution): AutoModerationActionExecutionEventData =
            with(entity) {
                AutoModerationActionExecutionEventData(
                    guildId = guildId,
                    action = AutoModerationActionData.from(action),
                    ruleId = ruleId,
                    ruleTriggerType = ruleTriggerType,
                    userId = userId,
                    channelId = channelId,
                    messageId = messageId,
                    alertSystemMessageId = alertSystemMessageId,
                    content = content,
                    matchedKeyword = matchedKeyword,
                    matchedContent = matchedContent,
                )
            }
    }
}
