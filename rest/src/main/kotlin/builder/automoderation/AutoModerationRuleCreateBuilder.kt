package dev.kord.rest.builder.automoderation

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.common.entity.AutoModerationRuleKeywordPresetType
import dev.kord.common.entity.DiscordAutoModerationRuleTriggerMetadata
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.AutoModerationRuleCreateRequest

@KordDsl
public sealed class AutoModerationRuleCreateBuilder(
    final override var name: String,
    final override var eventType: AutoModerationRuleEventType,
) : AutoModerationRuleBuilder, AuditRequestBuilder<AutoModerationRuleCreateRequest> {
    final override var reason: String? = null

    final override fun assignName(name: String) {
        this.name = name
    }

    final override fun assignEventType(eventType: AutoModerationRuleEventType) {
        this.eventType = eventType
    }

    protected open fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> = Optional.Missing()

    final override var actions: MutableList<AutoModerationActionBuilder> = mutableListOf()
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

@KordDsl
public class KeywordAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
) : AutoModerationRuleCreateBuilder(name, eventType), KeywordAutoModerationRuleBuilder {

    override var keywords: MutableList<String> = mutableListOf()
    override fun assignKeywords(keywords: MutableList<String>) {
        this.keywords = keywords
    }

    override fun buildTriggerMetadata(): Optional.Value<DiscordAutoModerationRuleTriggerMetadata> =
        DiscordAutoModerationRuleTriggerMetadata(keywordFilter = keywords.toList().optional()).optional()
}

@KordDsl
public class HarmfulLinkAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
) : AutoModerationRuleCreateBuilder(name, eventType), HarmfulLinkAutoModerationRuleBuilder

@KordDsl
public class SpamAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
) : AutoModerationRuleCreateBuilder(name, eventType), SpamAutoModerationRuleBuilder

@KordDsl
public class KeywordPresetAutoModerationRuleCreateBuilder(
    name: String,
    eventType: AutoModerationRuleEventType,
) : AutoModerationRuleCreateBuilder(name, eventType), KeywordPresetAutoModerationRuleBuilder {

    override var presets: MutableList<AutoModerationRuleKeywordPresetType> = mutableListOf()
    override fun assignPresets(presets: MutableList<AutoModerationRuleKeywordPresetType>) {
        this.presets = presets
    }

    private var _allowList: Optional<MutableList<String>> = Optional.Missing()
    override var allowList: MutableList<String>? by ::_allowList.delegate()

    override fun buildTriggerMetadata(): Optional.Value<DiscordAutoModerationRuleTriggerMetadata> =
        DiscordAutoModerationRuleTriggerMetadata(
            presets = presets.toList().optional(),
            allowList = _allowList.map { it.toList() },
        ).optional()
}
