package dev.kord.rest.builder.automoderation

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.common.entity.AutoModerationRuleKeywordPresetType
import dev.kord.common.entity.DiscordAutoModerationRuleTriggerMetadata
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.AutoModerationRuleModifyRequest

@KordDsl
public sealed class AutoModerationRuleModifyBuilder :
    AutoModerationRuleBuilder,
    AuditRequestBuilder<AutoModerationRuleModifyRequest> {
    final override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    final override var name: String? by ::_name.delegate()
    final override fun assignName(name: String) {
        this.name = name
    }

    private var _eventType: Optional<AutoModerationRuleEventType> = Optional.Missing()
    final override var eventType: AutoModerationRuleEventType? by ::_eventType.delegate()
    final override fun assignEventType(eventType: AutoModerationRuleEventType) {
        this.eventType = eventType
    }

    protected open fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> = Optional.Missing()

    private var _actions: Optional<MutableList<AutoModerationActionBuilder>> = Optional.Missing()
    final override var actions: MutableList<AutoModerationActionBuilder>? by ::_actions.delegate()
    final override fun assignActions(actions: MutableList<AutoModerationActionBuilder>) {
        this.actions = actions
    }

    private var _enabled: OptionalBoolean = OptionalBoolean.Missing
    final override var enabled: Boolean? by ::_enabled.delegate()

    private var _exemptRoles: Optional<MutableList<Snowflake>> = Optional.Missing()
    final override var exemptRoles: MutableList<Snowflake>? by ::_exemptRoles.delegate()

    private var _exemptChannels: Optional<MutableList<Snowflake>> = Optional.Missing()
    final override var exemptChannels: MutableList<Snowflake>? by ::_exemptChannels.delegate()

    final override fun toRequest(): AutoModerationRuleModifyRequest = AutoModerationRuleModifyRequest(
        name = _name,
        eventType = _eventType,
        triggerMetadata = buildTriggerMetadata(),
        actions = _actions.mapList { it.toRequest() },
        enabled = _enabled,
        exemptRoles = _exemptRoles.map { it.toList() },
        exemptChannels = _exemptChannels.map { it.toList() },
    )
}

@KordDsl
public class KeywordAutoModerationRuleModifyBuilder :
    AutoModerationRuleModifyBuilder(),
    KeywordAutoModerationRuleBuilder {

    private var _keywords: Optional<MutableList<String>> = Optional.Missing()
    override var keywords: MutableList<String>? by ::_keywords.delegate()
    override fun assignKeywords(keywords: MutableList<String>) {
        this.keywords = keywords
    }

    override fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> =
        _keywords.map { DiscordAutoModerationRuleTriggerMetadata(keywordFilter = it.toList().optional()) }
}

@Suppress("CanSealedSubClassBeObject") // has state in super class
@KordDsl
public class HarmfulLinkAutoModerationRuleModifyBuilder :
    AutoModerationRuleModifyBuilder(),
    HarmfulLinkAutoModerationRuleBuilder

@Suppress("CanSealedSubClassBeObject") // has state in super class
@KordDsl
public class SpamAutoModerationRuleModifyBuilder :
    AutoModerationRuleModifyBuilder(),
    SpamAutoModerationRuleBuilder

@KordDsl
public class KeywordPresetAutoModerationRuleModifyBuilder :
    AutoModerationRuleModifyBuilder(),
    KeywordPresetAutoModerationRuleBuilder {

    private var _presets: Optional<MutableList<AutoModerationRuleKeywordPresetType>> = Optional.Missing()
    override var presets: MutableList<AutoModerationRuleKeywordPresetType>? by ::_presets.delegate()
    override fun assignPresets(presets: MutableList<AutoModerationRuleKeywordPresetType>) {
        this.presets = presets
    }

    private var _allowList: Optional<MutableList<String>> = Optional.Missing()
    override var allowList: MutableList<String>? by ::_allowList.delegate()

    override fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> {
        val presets = _presets
        val allowList = _allowList
        return when {
            presets !is Optional.Missing || allowList !is Optional.Missing -> DiscordAutoModerationRuleTriggerMetadata(
                presets = presets.map { it.toList() },
                allowList = allowList.map { it.toList() },
            ).optional()
            else -> Optional.Missing()
        }
    }
}
