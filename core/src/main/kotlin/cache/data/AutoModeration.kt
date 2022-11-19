package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.map
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.serialization.Serializable

@Serializable
public data class AutoModerationRuleData(
    val id: Snowflake,
    val guildId: Snowflake,
    val name: String,
    val creatorId: Snowflake,
    val eventType: AutoModerationRuleEventType,
    val triggerType: AutoModerationRuleTriggerType,
    val triggerMetadata: AutoModerationRuleTriggerMetadataData,
    val actions: List<AutoModerationActionData>,
    val enabled: Boolean,
    val exemptRoles: List<Snowflake>,
    val exemptChannels: List<Snowflake>,
) {
    public companion object {
        public val description: DataDescription<AutoModerationRuleData, Snowflake> =
            description(AutoModerationRuleData::id)

        public fun from(rule: DiscordAutoModerationRule): AutoModerationRuleData = with(rule) {
            AutoModerationRuleData(
                id = id,
                guildId = guildId,
                name = name,
                creatorId = creatorId,
                eventType = eventType,
                triggerType = triggerType,
                triggerMetadata = AutoModerationRuleTriggerMetadataData.from(triggerMetadata),
                actions = actions.map { AutoModerationActionData.from(it) },
                enabled = enabled,
                exemptRoles = exemptRoles,
                exemptChannels = exemptChannels,
            )
        }
    }
}

@Serializable
public data class AutoModerationRuleTriggerMetadataData(
    val keywordFilter: Optional<List<String>> = Optional.Missing(),
    val regexPatterns: Optional<List<String>> = Optional.Missing(),
    val presets: Optional<List<AutoModerationRuleKeywordPresetType>> = Optional.Missing(),
    val allowList: Optional<List<String>> = Optional.Missing(),
    val mentionTotalLimit: OptionalInt = OptionalInt.Missing,
) {
    public companion object {
        public fun from(metadata: DiscordAutoModerationRuleTriggerMetadata): AutoModerationRuleTriggerMetadataData =
            with(metadata) {
                AutoModerationRuleTriggerMetadataData(
                    keywordFilter = keywordFilter,
                    regexPatterns = regexPatterns,
                    presets = presets,
                    allowList = allowList,
                    mentionTotalLimit = mentionTotalLimit,
                )
            }
    }
}

@Serializable
public data class AutoModerationActionData(
    val type: AutoModerationActionType,
    val metadata: Optional<AutoModerationActionMetadataData> = Optional.Missing(),
) {
    public companion object {
        public fun from(action: DiscordAutoModerationAction): AutoModerationActionData = with(action) {
            AutoModerationActionData(
                type = type,
                metadata = metadata.map { AutoModerationActionMetadataData.from(it) },
            )
        }
    }
}

@Serializable
public data class AutoModerationActionMetadataData(
    public val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    public val durationSeconds: Optional<DurationInSeconds> = Optional.Missing(),
) {
    public companion object {
        public fun from(metadata: DiscordAutoModerationActionMetadata): AutoModerationActionMetadataData =
            with(metadata) {
                AutoModerationActionMetadataData(
                    channelId = channelId,
                    durationSeconds = durationSeconds,
                )
            }
    }
}
