@file:GenerateKordEnum(
    name = "AutoModerationRuleTriggerType", valueType = INT,
    kDoc = "Characterizes the type of content which can trigger the rule.",
    docUrl = "https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-trigger-types",
    entries = [
        Entry("Keyword", intValue = 1, kDoc = "Check if content contains words from a user defined list of keywords."),
        Entry("Spam", intValue = 3, kDoc = "Check if content represents generic spam."),
        Entry(
            "KeywordPreset", intValue = 4,
            kDoc = "Check if content contains words from internal pre-defined wordsets."
        ),
        Entry("MentionSpam", intValue = 5, kDoc = "Check if content contains more unique mentions than allowed."),
    ],
)

@file:GenerateKordEnum(
    name = "AutoModerationRuleKeywordPresetType", valueType = INT,
    kDoc = "An internally pre-defined wordset which will be searched for in content.",
    docUrl = "https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-preset-types",
    entries = [
        Entry("Profanity", intValue = 1, kDoc = "Words that may be considered forms of swearing or cursing."),
        Entry("SexualContent", intValue = 2, kDoc = "Words that refer to sexually explicit behavior or activity."),
        Entry("Slurs", intValue = 3, kDoc = "Personal insults or words that may be considered hate speech."),
    ],
)

@file:GenerateKordEnum(
    name = "AutoModerationRuleEventType", valueType = INT,
    kDoc = "Indicates in what event context a rule should be checked.",
    docUrl = "https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-event-types",
    entries = [
        Entry("MessageSend", intValue = 1, kDoc = "When a member sends or edits a message in the guild."),
    ],
)

@file:GenerateKordEnum(
    name = "AutoModerationActionType", valueType = INT,
    kDoc = "The type of action.",
    docUrl = "https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-action-object-action-types",
    entries = [
        Entry(
            "BlockMessage", intValue = 1,
            kDoc = "Blocks a member's message and prevents it from being posted.\n\nA custom explanation can be " +
                "specified and shown to members whenever their message is blocked.",
        ),
        Entry("SendAlertMessage", intValue = 2, kDoc = "Logs user content to a specified channel."),
        Entry(
            "Timeout", intValue = 3,
            kDoc = "Timeout user for a specified duration.\n\nA [Timeout] action can only be set up for " +
                    "[Keyword][dev.kord.common.entity.AutoModerationRuleTriggerType.Keyword] and " +
                    "[MentionSpam][dev.kord.common.entity.AutoModerationRuleTriggerType.MentionSpam] rules. The " +
                    "[ModerateMembers][dev.kord.common.entity.Permission.ModerateMembers] permission is required to " +
                    "use the [Timeout] action type."
        ),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInSeconds
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordAutoModerationRule(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val name: String,
    @SerialName("creator_id")
    val creatorId: Snowflake,
    @SerialName("event_type")
    val eventType: AutoModerationRuleEventType,
    @SerialName("trigger_type")
    val triggerType: AutoModerationRuleTriggerType,
    @SerialName("trigger_metadata")
    val triggerMetadata: DiscordAutoModerationRuleTriggerMetadata,
    val actions: List<DiscordAutoModerationAction>,
    val enabled: Boolean,
    @SerialName("exempt_roles")
    val exemptRoles: List<Snowflake>,
    @SerialName("exempt_channels")
    val exemptChannels: List<Snowflake>,
)

@Serializable
public data class DiscordAutoModerationRuleTriggerMetadata(
    @SerialName("keyword_filter")
    val keywordFilter: Optional<List<String>> = Optional.Missing(),
    @SerialName("regex_patterns")
    val regexPatterns: Optional<List<String>> = Optional.Missing(),
    val presets: Optional<List<AutoModerationRuleKeywordPresetType>> = Optional.Missing(),
    @SerialName("allow_list")
    val allowList: Optional<List<String>> = Optional.Missing(),
    @SerialName("mention_total_limit")
    val mentionTotalLimit: OptionalInt = OptionalInt.Missing,
    @SerialName("mention_raid_protection_enabled")
    val mentionRaidProtectionEnabled: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable
public data class DiscordAutoModerationAction(
    val type: AutoModerationActionType,
    val metadata: Optional<DiscordAutoModerationActionMetadata> = Optional.Missing(),
)

@Serializable
public data class DiscordAutoModerationActionMetadata(
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("duration_seconds")
    val durationSeconds: Optional<DurationInSeconds> = Optional.Missing(),
    @SerialName("custom_message")
    val customMessage: Optional<String> = Optional.Missing(),
)
