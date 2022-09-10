package dev.kord.rest.builder.automoderation

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.AutoModerationActionType.*
import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.common.entity.AutoModerationRuleKeywordPresetType
import dev.kord.common.entity.AutoModerationRuleTriggerType
import dev.kord.common.entity.AutoModerationRuleTriggerType.*
import dev.kord.common.entity.Permission.ModerateMembers
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.AuditBuilder
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.time.Duration

// the `val propertyName` and `fun assignPropertyName` pattern let us effectively have `var`s with different
// nullability for getter and setter that allows a common supertype for create and modify builders


/**
 * An [AuditBuilder] for building
 * [Auto Moderation Rules](https://discord.com/developers/docs/resources/auto-moderation).
 *
 * Auto Moderation is a feature which allows each guild to set up rules that trigger based on some criteria. For
 * example, a rule can trigger whenever a message contains a specific keyword.
 *
 * Rules can be configured to automatically execute actions whenever they trigger. For example, if a user tries to send
 * a message which contains a certain keyword, a rule can trigger and block the message before it is sent.
 */
@KordDsl
public sealed interface AutoModerationRuleBuilder : AuditBuilder {

    /** The rule name. */
    public val name: String?

    /** Use this to set [name][AutoModerationRuleBuilder.name] for [AutoModerationRuleBuilder]. */
    public fun assignName(name: String)

    /** The rule [event type][AutoModerationRuleEventType]. */
    public val eventType: AutoModerationRuleEventType?

    /** Use this to set [eventType][AutoModerationRuleBuilder.eventType] for [AutoModerationRuleBuilder]. */
    public fun assignEventType(eventType: AutoModerationRuleEventType)

    /**
     * The rule [trigger type][AutoModerationRuleTriggerType].
     *
     * This might be `null` if the function that created this builder doesn't know the trigger type based on the
     * type system.
     */
    public val triggerType: AutoModerationRuleTriggerType?

    /** The actions which will execute when the rule is triggered. */
    public val actions: MutableList<AutoModerationActionBuilder>?

    /** Use this to set [actions][AutoModerationRuleBuilder.actions] for [AutoModerationRuleBuilder]. */
    public fun assignActions(actions: MutableList<AutoModerationActionBuilder>)

    /** Whether the rule is enabled (`false` by default). */
    public var enabled: Boolean?

    /** The IDs of the roles that should not be affected by the rule (maximum of 20). */
    public var exemptRoles: MutableList<Snowflake>?

    /** The IDs of the channels that should not be affected by the rule (maximum of 50). */
    public var exemptChannels: MutableList<Snowflake>?
}

/** Add a [BlockMessage] action which will execute whenever the rule is triggered. */
public inline fun AutoModerationRuleBuilder.blockMessage(
    builder: BlockMessageAutoModerationActionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val action = BlockMessageAutoModerationActionBuilder().apply(builder)
    actions?.add(action) ?: assignActions(mutableListOf(action))
}

/**
 * Add a [SendAlertMessage] action which will execute whenever the rule is triggered.
 *
 * @param channelId the ID of the channel to which user content should be logged.
 */
public inline fun AutoModerationRuleBuilder.sendAlertMessage(
    channelId: Snowflake,
    builder: SendAlertMessageAutoModerationActionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val action = SendAlertMessageAutoModerationActionBuilder(channelId).apply(builder)
    actions?.add(action) ?: assignActions(mutableListOf(action))
}

/** Exempt a [role][roleId] from being affected by the rule (maximum of 20). */
public fun AutoModerationRuleBuilder.exemptRole(roleId: Snowflake) {
    exemptRoles?.add(roleId) ?: run { exemptRoles = mutableListOf(roleId) }
}

/** Exempt a [channel][channelId] from being affected by the rule (maximum of 50). */
public fun AutoModerationRuleBuilder.exemptChannel(channelId: Snowflake) {
    exemptChannels?.add(channelId) ?: run { exemptChannels = mutableListOf(channelId) }
}


/** An [AutoModerationRuleBuilder] with a non-null [triggerType]. */
@KordDsl
public sealed interface TypedAutoModerationRuleBuilder : AutoModerationRuleBuilder {

    /** The rule [trigger type][AutoModerationRuleTriggerType]. */
    override val triggerType: AutoModerationRuleTriggerType
}


/** An [AutoModerationRuleBuilder] for building rules that can have a [Timeout] action. */
@KordDsl
public sealed interface TimeoutAutoModerationRuleBuilder : TypedAutoModerationRuleBuilder

/**
 * Add a [Timeout] action which will execute whenever the rule is triggered.
 *
 * The [ModerateMembers] permission is required to use this action.
 *
 * @param duration the timeout duration (maximum of 2419200 seconds (4 weeks)).
 */
public inline fun TimeoutAutoModerationRuleBuilder.timeout(
    duration: Duration,
    builder: TimeoutAutoModerationActionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val action = TimeoutAutoModerationActionBuilder(duration).apply(builder)
    actions?.add(action) ?: assignActions(mutableListOf(action))
}


/** An [AutoModerationRuleBuilder] for building rules with trigger type [Keyword]. */
@KordDsl
public sealed interface KeywordAutoModerationRuleBuilder : TimeoutAutoModerationRuleBuilder {

    override val triggerType: Keyword get() = Keyword

    /**
     * Substrings which will be searched for in content.
     *
     * A keyword can be a phrase which contains multiple words. Wildcard symbols can be used to customize how each
     * keyword will be matched. See
     * [keyword matching strategies](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies).
     */
    public val keywords: MutableList<String>?

    /** Use this to set [keywords][KeywordAutoModerationRuleBuilder.keywords] for [KeywordAutoModerationRuleBuilder]. */
    public fun assignKeywords(keywords: MutableList<String>)
}

/**
 * Add a [keyword] to [keywords][KeywordAutoModerationRuleBuilder.keywords].
 *
 * A keyword can be a phrase which contains multiple words. Wildcard symbols can be used to customize how each
 * keyword will be matched. See
 * [keyword matching strategies](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies).
 */
public fun KeywordAutoModerationRuleBuilder.keyword(keyword: String) {
    keywords?.add(keyword) ?: assignKeywords(mutableListOf(keyword))
}

/**
 * Add a [keyword] with keyword matching strategy
 * [Prefix](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * to [keywords][KeywordAutoModerationRuleBuilder.keywords].
 *
 * A keyword can be a phrase which contains multiple words.
 */
public fun KeywordAutoModerationRuleBuilder.prefixKeyword(keyword: String) {
    keyword("$keyword*")
}

/**
 * Add a [keyword] with keyword matching strategy
 * [Suffix](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * to [keywords][KeywordAutoModerationRuleBuilder.keywords].
 *
 * A keyword can be a phrase which contains multiple words.
 */
public fun KeywordAutoModerationRuleBuilder.suffixKeyword(keyword: String) {
    keyword("*$keyword")
}

/**
 * Add a [keyword] with keyword matching strategy
 * [Anywhere](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * to [keywords][KeywordAutoModerationRuleBuilder.keywords].
 *
 * A keyword can be a phrase which contains multiple words.
 */
public fun KeywordAutoModerationRuleBuilder.anywhereKeyword(keyword: String) {
    keyword("*$keyword*")
}


/**
 * An [AutoModerationRuleBuilder] for building rules with trigger type [Spam].
 *
 * The [Spam] trigger type is not yet released, so it cannot be used in most servers.
 */
@KordDsl
@KordExperimental
public sealed interface SpamAutoModerationRuleBuilder : TypedAutoModerationRuleBuilder {
    override val triggerType: Spam get() = Spam
}


/** An [AutoModerationRuleBuilder] for building rules with trigger type [KeywordPreset]. */
@KordDsl
public sealed interface KeywordPresetAutoModerationRuleBuilder : TypedAutoModerationRuleBuilder {

    override val triggerType: KeywordPreset get() = KeywordPreset

    /** The internally pre-defined wordsets which will be searched for in content. */
    public val presets: MutableList<AutoModerationRuleKeywordPresetType>?

    /**
     * Use this to set [presets][KeywordPresetAutoModerationRuleBuilder.presets] for
     * [KeywordPresetAutoModerationRuleBuilder].
     */
    public fun assignPresets(presets: MutableList<AutoModerationRuleKeywordPresetType>)

    /**
     * Substrings which will be exempt from triggering the [presets].
     *
     * A keyword can be a phrase which contains multiple words.
     */
    public var allowedKeywords: MutableList<String>?
}

/** Add a [preset] to [presets][KeywordPresetAutoModerationRuleBuilder.presets]. */
public fun KeywordPresetAutoModerationRuleBuilder.preset(preset: AutoModerationRuleKeywordPresetType) {
    presets?.add(preset) ?: assignPresets(mutableListOf(preset))
}

/**
 * Add a [keyword] to [allowedKeywords][KeywordPresetAutoModerationRuleBuilder.allowedKeywords].
 *
 * A keyword can be a phrase which contains multiple words.
 */
public fun KeywordPresetAutoModerationRuleBuilder.allowKeyword(keyword: String) {
    allowedKeywords?.add(keyword) ?: run { allowedKeywords = mutableListOf(keyword) }
}


/**
 * An [AutoModerationRuleBuilder] for building rules with trigger type [MentionSpam].
 *
 * The [MentionSpam] trigger type is not yet released, so it cannot be used in most servers.
 */
@KordDsl
@KordExperimental
public sealed interface MentionSpamAutoModerationRuleBuilder : TimeoutAutoModerationRuleBuilder {

    override val triggerType: MentionSpam get() = MentionSpam

    /** Total number of mentions (role & user) allowed per message (maximum of 50). */
    public val mentionLimit: Int?

    /**
     * Use this to set [mentionLimit][MentionSpamAutoModerationRuleBuilder.mentionLimit] for
     * [MentionSpamAutoModerationRuleBuilder].
     */
    public fun assignMentionLimit(mentionLimit: Int)
}
