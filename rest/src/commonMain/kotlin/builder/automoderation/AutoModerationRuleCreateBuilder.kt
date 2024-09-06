package dev.kord.rest.builder.automoderation

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.common.entity.AutoModerationRuleKeywordPresetType
import dev.kord.common.entity.DiscordAutoModerationRuleTriggerMetadata
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.AutoModerationRuleCreateRequest

/** An [AutoModerationRuleBuilder] for building [AutoModerationRuleCreateRequest]s. */
@KordDsl
public sealed class AutoModerationRuleCreateBuilder(
    final override var name: String,
    final override var eventType: AutoModerationRuleEventType,
) : TypedAutoModerationRuleBuilder, AuditRequestBuilder<AutoModerationRuleCreateRequest> {
    final override var reason: String? = null

    /** @suppress Use `this.name = name` instead. */
    final override fun assignName(name: String) {
        this.name = name
    }

    /** @suppress Use `this.eventType = eventType` instead. */
    final override fun assignEventType(eventType: AutoModerationRuleEventType) {
        this.eventType = eventType
    }

    protected open fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> = Optional.Missing()

    final override var actions: MutableList<AutoModerationActionBuilder> = mutableListOf()

    /** @suppress Use `this.actions = actions` instead. */
    final override fun assignActions(actions: MutableList<AutoModerationActionBuilder>) {
        this.actions = actions
    }

    private var _enabled: OptionalBoolean = OptionalBoolean.Missing
    final override var enabled: Boolean? by ::_enabled.delegate()

    private var _exemptRoles: Optional<MutableList<Snowflake>> = Optional.Missing()
    final override var exemptRoles: MutableList<Snowflake>? by ::_exemptRoles.delegate()

    private var _exemptChannels: Optional<MutableList<Snowflake>> = Optional.Missing()
    final override var exemptChannels: MutableList<Snowflake>? by ::_exemptChannels.delegate()

    final override fun toRequest(): AutoModerationRuleCreateRequest = AutoModerationRuleCreateRequest(
        name = name,
        eventType = eventType,
        triggerType = triggerType,
        triggerMetadata = buildTriggerMetadata(),
        actions = actions.map { it.toRequest() },
        enabled = _enabled,
        exemptRoles = _exemptRoles.mapCopy(),
        exemptChannels = _exemptChannels.mapCopy(),
    )
}

/** A [KeywordAutoModerationRuleBuilder] for building [AutoModerationRuleCreateRequest]s. */
@KordDsl
public class KeywordAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
) : AutoModerationRuleCreateBuilder(name, eventType), KeywordAutoModerationRuleBuilder {

    private var _keywords: Optional<MutableList<String>> = Optional.Missing()
    override var keywords: MutableList<String>? by ::_keywords.delegate()

    private var _regexPatterns: Optional<MutableList<String>> = Optional.Missing()
    override var regexPatterns: MutableList<String>? by ::_regexPatterns.delegate()

    private var _allowedKeywords: Optional<MutableList<String>> = Optional.Missing()
    override var allowedKeywords: MutableList<String>? by ::_allowedKeywords.delegate()

    // triggerMetadata is required for Keyword rules
    override fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> =
        DiscordAutoModerationRuleTriggerMetadata(
            keywordFilter = _keywords.mapCopy(),
            regexPatterns = _regexPatterns.mapCopy(),
            allowList = _allowedKeywords.mapCopy(),
        ).optional()
}

/** A [SpamAutoModerationRuleBuilder] for building [AutoModerationRuleCreateRequest]s. */
@KordDsl
public class SpamAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
) : AutoModerationRuleCreateBuilder(name, eventType), SpamAutoModerationRuleBuilder

/** A [KeywordPresetAutoModerationRuleBuilder] for building [AutoModerationRuleCreateRequest]s. */
@KordDsl
public class KeywordPresetAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
) : AutoModerationRuleCreateBuilder(name, eventType), KeywordPresetAutoModerationRuleBuilder {

    private var _presets: Optional<MutableList<AutoModerationRuleKeywordPresetType>> = Optional.Missing()
    override var presets: MutableList<AutoModerationRuleKeywordPresetType>? by ::_presets.delegate()

    /** @suppress Use `this.presets = presets` instead. */
    @Deprecated(
        "Use 'this.presets = presets' instead. The deprecation level will be raised to ERROR in 0.16.0, to HIDDEN in " +
            "0.17.0, and this declaration will be removed in 0.18.0.",
        ReplaceWith("this.run { this@run.presets = presets }", imports = ["kotlin.run"]),
        DeprecationLevel.WARNING,
    )
    override fun assignPresets(presets: MutableList<AutoModerationRuleKeywordPresetType>) {
        this.presets = presets
    }

    private var _allowedKeywords: Optional<MutableList<String>> = Optional.Missing()
    override var allowedKeywords: MutableList<String>? by ::_allowedKeywords.delegate()

    // triggerMetadata is required for KeywordPreset rules
    override fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> =
        DiscordAutoModerationRuleTriggerMetadata(
            presets = _presets.mapCopy(),
            allowList = _allowedKeywords.mapCopy(),
        ).optional()
}

/** A [MentionSpamAutoModerationRuleBuilder] for building [AutoModerationRuleCreateRequest]s. */
@KordDsl
public class MentionSpamAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
) : AutoModerationRuleCreateBuilder(name, eventType), MentionSpamAutoModerationRuleBuilder {

    private var _mentionLimit: OptionalInt = OptionalInt.Missing
    override var mentionLimit: Int? by ::_mentionLimit.delegate()

    private var _mentionRaidProtectionEnabled: OptionalBoolean = OptionalBoolean.Missing
    override var mentionRaidProtectionEnabled: Boolean? by ::_mentionRaidProtectionEnabled.delegate()

    // triggerMetadata is required for MentionSpam rules
    override fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> =
        DiscordAutoModerationRuleTriggerMetadata(
            mentionTotalLimit = _mentionLimit,
            mentionRaidProtectionEnabled = _mentionRaidProtectionEnabled,
        ).optional()
}
