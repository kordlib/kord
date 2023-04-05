package dev.kord.rest.json.request

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AutoModerationRuleCreateRequest(
    val name: String,
    @SerialName("event_type")
    val eventType: AutoModerationRuleEventType,
    @SerialName("trigger_type")
    val triggerType: AutoModerationRuleTriggerType,
    @SerialName("trigger_metadata")
    val triggerMetadata: Optional<DiscordAutoModerationRuleTriggerMetadata> = Optional.Missing(),
    val actions: List<DiscordAutoModerationAction>,
    val enabled: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("exempt_roles")
    val exemptRoles: Optional<List<Snowflake>> = Optional.Missing(),
    @SerialName("exempt_channels")
    val exemptChannels: Optional<List<Snowflake>> = Optional.Missing(),
)

@Serializable
public data class AutoModerationRuleModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    @SerialName("event_type")
    val eventType: Optional<AutoModerationRuleEventType> = Optional.Missing(),
    @SerialName("trigger_metadata")
    val triggerMetadata: Optional<DiscordAutoModerationRuleTriggerMetadata> = Optional.Missing(),
    val actions: Optional<List<DiscordAutoModerationAction>> = Optional.Missing(),
    val enabled: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("exempt_roles")
    val exemptRoles: Optional<List<Snowflake>> = Optional.Missing(),
    @SerialName("exempt_channels")
    val exemptChannels: Optional<List<Snowflake>> = Optional.Missing(),
)
