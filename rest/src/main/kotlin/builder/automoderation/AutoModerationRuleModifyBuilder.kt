package dev.kord.rest.builder.automoderation

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.AutoModerationRuleModifyRequest
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

/** An [AutoModerationRuleBuilder] for building [AutoModerationRuleModifyRequest]s. */
@KordDsl
public sealed class AutoModerationRuleModifyBuilder :
    AutoModerationRuleBuilder,
    AuditRequestBuilder<AutoModerationRuleModifyRequest> {
    final override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    final override var name: String? by ::_name.delegate()

    /** @suppress Use `this.name = name` instead. */
    final override fun assignName(name: String) {
        this.name = name
    }

    private var _eventType: Optional<AutoModerationRuleEventType> = Optional.Missing()
    final override var eventType: AutoModerationRuleEventType? by ::_eventType.delegate()

    /** @suppress Use `this.eventType = eventType` instead. */
    final override fun assignEventType(eventType: AutoModerationRuleEventType) {
        this.eventType = eventType
    }

    protected open fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> = Optional.Missing()

    private var _actions: Optional<MutableList<AutoModerationActionBuilder>> = Optional.Missing()
    final override var actions: MutableList<AutoModerationActionBuilder>? by ::_actions.delegate()

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

    final override fun toRequest(): AutoModerationRuleModifyRequest = AutoModerationRuleModifyRequest(
        name = _name,
        eventType = _eventType,
        triggerMetadata = buildTriggerMetadata(),
        actions = _actions.mapList { it.toRequest() },
        enabled = _enabled,
        exemptRoles = _exemptRoles.mapCopy(),
        exemptChannels = _exemptChannels.mapCopy(),
    )
}

/** An [AutoModerationRuleModifyBuilder] with an always `null` [triggerType]. */
@KordDsl
public class UntypedAutoModerationRuleModifyBuilder : AutoModerationRuleModifyBuilder() {

    /**
     * This is always `null`, the function that created this builder doesn't know the
     * [trigger type][AutoModerationRuleTriggerType] based on the type system.
     */
    override val triggerType: Nothing? get() = null
}

/** A [KeywordAutoModerationRuleBuilder] for building [AutoModerationRuleModifyRequest]s. */
@KordDsl
public class KeywordAutoModerationRuleModifyBuilder :
    AutoModerationRuleModifyBuilder(),
    KeywordAutoModerationRuleBuilder {

    private var _keywords: Optional<MutableList<String>> = Optional.Missing()
    override var keywords: MutableList<String>? by ::_keywords.delegate()

    private var _regexPatterns: Optional<MutableList<String>> = Optional.Missing()
    override var regexPatterns: MutableList<String>? by ::_regexPatterns.delegate()

    private var _allowedKeywords: Optional<MutableList<String>> = Optional.Missing()
    override var allowedKeywords: MutableList<String>? by ::_allowedKeywords.delegate()

    override fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> {
        val keywords = _keywords
        val regexPatterns = _regexPatterns
        val allowedKeywords = _allowedKeywords
        return ifAnyPresent(keywords, regexPatterns, allowedKeywords) {
            DiscordAutoModerationRuleTriggerMetadata(
                keywordFilter = keywords.mapCopy(),
                regexPatterns = regexPatterns.mapCopy(),
                allowList = allowedKeywords.mapCopy(),
            )
        }
    }
}

/** A [SpamAutoModerationRuleBuilder] for building [AutoModerationRuleModifyRequest]s. */
@Suppress("CanSealedSubClassBeObject") // has state in super class
@KordDsl
public class SpamAutoModerationRuleModifyBuilder :
    AutoModerationRuleModifyBuilder(),
    SpamAutoModerationRuleBuilder

/** A [KeywordPresetAutoModerationRuleBuilder] for building [AutoModerationRuleModifyRequest]s. */
@KordDsl
public class KeywordPresetAutoModerationRuleModifyBuilder :
    AutoModerationRuleModifyBuilder(),
    KeywordPresetAutoModerationRuleBuilder {

    private var _presets: Optional<MutableList<AutoModerationRuleKeywordPresetType>> = Optional.Missing()
    override var presets: MutableList<AutoModerationRuleKeywordPresetType>? by ::_presets.delegate()

    /** @suppress Use `this.presets = presets` instead. */
    override fun assignPresets(presets: MutableList<AutoModerationRuleKeywordPresetType>) {
        this.presets = presets
    }

    private var _allowedKeywords: Optional<MutableList<String>> = Optional.Missing()
    override var allowedKeywords: MutableList<String>? by ::_allowedKeywords.delegate()

    override fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> {
        val presets = _presets
        val allowedKeywords = _allowedKeywords
        return ifAnyPresent(presets, allowedKeywords) {
            DiscordAutoModerationRuleTriggerMetadata(
                presets = presets.mapCopy(),
                allowList = allowedKeywords.mapCopy(),
            )
        }
    }
}

/** A [MentionSpamAutoModerationRuleBuilder] for building [AutoModerationRuleModifyRequest]s. */
@KordDsl
public class MentionSpamAutoModerationRuleModifyBuilder :
    AutoModerationRuleModifyBuilder(),
    MentionSpamAutoModerationRuleBuilder {

    private var _mentionLimit: OptionalInt = OptionalInt.Missing
    override var mentionLimit: Int? by ::_mentionLimit.delegate()

    /** @suppress Use `this.mentionLimit = mentionLimit` instead. */
    override fun assignMentionLimit(mentionLimit: Int) {
        this.mentionLimit = mentionLimit
    }

    override fun buildTriggerMetadata(): Optional<DiscordAutoModerationRuleTriggerMetadata> =
        when (val limit = _mentionLimit) {
            OptionalInt.Missing -> Optional.Missing()
            is OptionalInt.Value -> DiscordAutoModerationRuleTriggerMetadata(mentionTotalLimit = limit).optional()
        }
}

private inline fun <T : Any> ifAnyPresent(vararg optionals: Optional<*>, block: () -> T): Optional<T> {
    contract { callsInPlace(block, AT_MOST_ONCE) }

    return if (optionals.any { it is Optional.Value }) {
        Optional.Value(block())
    } else {
        Optional.Missing()
    }
}
