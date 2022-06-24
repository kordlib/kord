package dev.kord.common.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordAutoModRule(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val name: String,
    @SerialName("creator_id")
    val creatorId: Snowflake,
    @SerialName("event_type")
    val eventType: AutoModRuleEventType,
    @SerialName("trigger_type")
    val triggerType: AutoModRuleTriggerType,
    @SerialName("trigger_metadata")
    val triggerMetadata: AutoModTriggerMetadata,
    val enabled: Boolean,
    @SerialName("exempt_roles")
    val exemptRoles: List<Snowflake>,
    @SerialName("exempt_channels")
    val exemptChannels: List<Snowflake>

)

@Serializable
public sealed  class AutoModRuleEventType(value: Int) {
    public object Keyword: AutoModRuleEventType(1)
    public object HarmfulLink: AutoModRuleEventType(2)
    public object Spam: AutoModRuleEventType(3)
    public object KeywordPreset: AutoModRuleEventType(4)

}

@Serializable
public sealed class AutoModRuleTriggerType(value: Int) {
    public object MessageSend: AutoModRuleTriggerType(1)
}

@Serializable
public data class AutoModTriggerMetadata(
    public val keywordFilter: List<String>,
    public val presets: List<AutoModPresetType>
)

@Serializable
public sealed class AutoModPresetType(value: Int) {
    public object Profanity : AutoModPresetType(1)
    public object SexualContent : AutoModPresetType(2)
    public object Slurs: AutoModPresetType(3)
}

public sealed class AutoModActionType(value: Int) {
    public object BlockMessage: AutoModActionType(1)
    public object SendAlertMessage: AutoModActionType(2)
    public object Timeout: AutoModActionType(3)
}
@Serializable
public class AutoModAction(
    public val type: AutoModActionType,
    @SerialName("action_metadata")
    public val actionMetadata: AutoModActionMetadata
)
@Serializable
public class AutoModActionMetadata(
    public val channelId: Snowflake,
    @SerialName("duration_seconds")
    public val durationSeconds: Int
)