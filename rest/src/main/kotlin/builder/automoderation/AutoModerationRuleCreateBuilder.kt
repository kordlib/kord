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
        exemptRoles = _exemptRoles.map { it.toList() },
        exemptChannels = _exemptChannels.map { it.toList() },
    )
}

/** A [KeywordAutoModerationRuleBuilder] for building [AutoModerationRuleCreateRequest]s. */
@KordDsl
public class KeywordAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
) : AutoModerationRuleCreateBuilder(name, eventType), KeywordAutoModerationRuleBuilder {

    override var keywords: MutableList<String> = mutableListOf()

    /** @suppress Use `this.keywords = keywords` instead. */
    override fun assignKeywords(keywords: MutableList<String>) {
        this.keywords = keywords
    }

    override fun buildTriggerMetadata(): Optional.Value<DiscordAutoModerationRuleTriggerMetadata> =
        DiscordAutoModerationRuleTriggerMetadata(keywordFilter = keywords.toList().optional()).optional()
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

    override var presets: MutableList<AutoModerationRuleKeywordPresetType> = mutableListOf()

    /** @suppress Use `this.presets = presets` instead. */
    override fun assignPresets(presets: MutableList<AutoModerationRuleKeywordPresetType>) {
        this.presets = presets
    }

    private var _allowedKeywords: Optional<MutableList<String>> = Optional.Missing()
    override var allowedKeywords: MutableList<String>? by ::_allowedKeywords.delegate()

    override fun buildTriggerMetadata(): Optional.Value<DiscordAutoModerationRuleTriggerMetadata> =
        DiscordAutoModerationRuleTriggerMetadata(
            presets = presets.toList().optional(),
            allowList = _allowedKeywords.map { it.toList() },
        ).optional()
}

/** A [MentionSpamAutoModerationRuleBuilder] for building [AutoModerationRuleCreateRequest]s. */
@KordDsl
public class MentionSpamAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
    override var mentionLimit: Int,
) : AutoModerationRuleCreateBuilder(name, eventType), MentionSpamAutoModerationRuleBuilder {

    /** @suppress Use `this.mentionLimit = mentionLimit` instead. */
    override fun assignMentionLimit(mentionLimit: Int) {
        this.mentionLimit = mentionLimit
    }

    override fun buildTriggerMetadata(): Optional.Value<DiscordAutoModerationRuleTriggerMetadata> =
        DiscordAutoModerationRuleTriggerMetadata(mentionTotalLimit = mentionLimit.optionalInt()).optional()
}
