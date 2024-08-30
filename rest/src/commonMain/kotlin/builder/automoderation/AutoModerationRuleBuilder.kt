package dev.kord.rest.builder.automoderation

import dev.kord.common.annotation.KordDsl
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


// -------- interfaces for supported actions shared between rule types --------

/** An [AutoModerationRuleBuilder] for building rules that can have a [BlockMessage] action. */
@KordDsl
public sealed interface BlockMessageAutoModerationRuleBuilder : TypedAutoModerationRuleBuilder

/** Add a [BlockMessage] action which will execute whenever the rule is triggered. */
public inline fun BlockMessageAutoModerationRuleBuilder.blockMessage(
    builder: BlockMessageAutoModerationActionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val action = BlockMessageAutoModerationActionBuilder().apply(builder)
    actions?.add(action) ?: assignActions(mutableListOf(action))
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


// -------- interfaces for supported options shared between rule types --------

/** An [AutoModerationRuleBuilder] for building rules that can have [keywords] and [regexPatterns]. */
@KordDsl
public sealed interface FilteredKeywordsAutoModerationRuleBuilder : TypedAutoModerationRuleBuilder {

    /**
     * Substrings which will be searched for in content (maximum of 1000).
     *
     * A keyword can be a phrase which contains multiple words.
     * [Wildcard symbols](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
     * can be used to customize how each keyword will be matched. Each keyword must be 60 characters or less.
     */
    public var keywords: MutableList<String>?

    /**
     * Regular expression patterns which will be matched against content (maximum of 10).
     *
     * Only Rust flavored regex is currently supported. Each regex pattern must be 260 characters or less.
     */
    public var regexPatterns: MutableList<String>?
}

/**
 * Add a [keyword] to [keywords][FilteredKeywordsAutoModerationRuleBuilder.keywords] (maximum of 1000).
 *
 * A keyword can be a phrase which contains multiple words.
 * [Wildcard symbols](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * can be used to customize how each keyword will be matched. Each keyword must be 60 characters or less.
 */
public fun FilteredKeywordsAutoModerationRuleBuilder.keyword(keyword: String) {
    keywords?.add(keyword) ?: run { keywords = mutableListOf(keyword) }
}

/**
 * Add a [keyword] with keyword matching strategy
 * [Prefix](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * to [keywords][FilteredKeywordsAutoModerationRuleBuilder.keywords] (maximum of 1000).
 *
 * A keyword can be a phrase which contains multiple words. Each keyword must be 60 characters or less.
 */
public fun FilteredKeywordsAutoModerationRuleBuilder.prefixKeyword(keyword: String) {
    keyword("$keyword*")
}

/**
 * Add a [keyword] with keyword matching strategy
 * [Suffix](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * to [keywords][FilteredKeywordsAutoModerationRuleBuilder.keywords] (maximum of 1000).
 *
 * A keyword can be a phrase which contains multiple words. Each keyword must be 60 characters or less.
 */
public fun FilteredKeywordsAutoModerationRuleBuilder.suffixKeyword(keyword: String) {
    keyword("*$keyword")
}

/**
 * Add a [keyword] with keyword matching strategy
 * [Anywhere](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * to [keywords][FilteredKeywordsAutoModerationRuleBuilder.keywords] (maximum of 1000).
 *
 * A keyword can be a phrase which contains multiple words. Each keyword must be 60 characters or less.
 */
public fun FilteredKeywordsAutoModerationRuleBuilder.anywhereKeyword(keyword: String) {
    keyword("*$keyword*")
}

/**
 * Add a [pattern] to [regexPatterns][FilteredKeywordsAutoModerationRuleBuilder.regexPatterns] (maximum of 10).
 *
 * Only Rust flavored regex is currently supported. Each regex pattern must be 260 characters or less.
 */
public fun FilteredKeywordsAutoModerationRuleBuilder.regexPattern(pattern: String) {
    regexPatterns?.add(pattern) ?: run { regexPatterns = mutableListOf(pattern) }
}


/** An [AutoModerationRuleBuilder] for building rules that can have [allowedKeywords]. */
@KordDsl
public sealed interface AllowedKeywordsAutoModerationRuleBuilder : TypedAutoModerationRuleBuilder {

    /**
     * Substrings which should not trigger the rule.
     *
     * A keyword can be a phrase which contains multiple words.
     * [Wildcard symbols](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
     * can be used to customize how each keyword will be matched. Each keyword must be 60 characters or less.
     */
    public var allowedKeywords: MutableList<String>?
}

/**
 * Add a [keyword] to [allowedKeywords][AllowedKeywordsAutoModerationRuleBuilder.allowedKeywords].
 *
 * A keyword can be a phrase which contains multiple words.
 * [Wildcard symbols](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * can be used to customize how each keyword will be matched. Each keyword must be 60 characters or less.
 */
public fun AllowedKeywordsAutoModerationRuleBuilder.allowKeyword(keyword: String) {
    allowedKeywords?.add(keyword) ?: run { allowedKeywords = mutableListOf(keyword) }
}

/**
 * Add a [keyword] with keyword matching strategy
 * [Prefix](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * to [allowedKeywords][AllowedKeywordsAutoModerationRuleBuilder.allowedKeywords].
 *
 * A keyword can be a phrase which contains multiple words. Each keyword must be 60 characters or less.
 */
public fun AllowedKeywordsAutoModerationRuleBuilder.allowPrefixKeyword(keyword: String) {
    allowKeyword("$keyword*")
}

/**
 * Add a [keyword] with keyword matching strategy
 * [Suffix](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * to [allowedKeywords][AllowedKeywordsAutoModerationRuleBuilder.allowedKeywords].
 *
 * A keyword can be a phrase which contains multiple words. Each keyword must be 60 characters or less.
 */
public fun AllowedKeywordsAutoModerationRuleBuilder.allowSuffixKeyword(keyword: String) {
    allowKeyword("*$keyword")
}

/**
 * Add a [keyword] with keyword matching strategy
 * [Anywhere](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies)
 * to [allowedKeywords][AllowedKeywordsAutoModerationRuleBuilder.allowedKeywords].
 *
 * A keyword can be a phrase which contains multiple words. Each keyword must be 60 characters or less.
 */
public fun AllowedKeywordsAutoModerationRuleBuilder.allowAnywhereKeyword(keyword: String) {
    allowKeyword("*$keyword*")
}


// -------- builders for concrete rule types --------

/** An [AutoModerationRuleBuilder] for building rules with trigger type [Keyword]. */
@KordDsl
public sealed interface KeywordAutoModerationRuleBuilder :
    BlockMessageAutoModerationRuleBuilder,
    TimeoutAutoModerationRuleBuilder,
    FilteredKeywordsAutoModerationRuleBuilder,
    AllowedKeywordsAutoModerationRuleBuilder {

    override val triggerType: Keyword get() = Keyword
}


/** An [AutoModerationRuleBuilder] for building rules with trigger type [Spam]. */
@KordDsl
public sealed interface SpamAutoModerationRuleBuilder : BlockMessageAutoModerationRuleBuilder {
    override val triggerType: Spam get() = Spam
}


/** An [AutoModerationRuleBuilder] for building rules with trigger type [KeywordPreset]. */
@KordDsl
public sealed interface KeywordPresetAutoModerationRuleBuilder :
    BlockMessageAutoModerationRuleBuilder,
    AllowedKeywordsAutoModerationRuleBuilder {

    override val triggerType: KeywordPreset get() = KeywordPreset

    /** The internally pre-defined wordsets which will be searched for in content. */
    public val presets: MutableList<AutoModerationRuleKeywordPresetType>?

    /**
     * Use this to set [presets][KeywordPresetAutoModerationRuleBuilder.presets] for
     * [KeywordPresetAutoModerationRuleBuilder].
     */
    public fun assignPresets(presets: MutableList<AutoModerationRuleKeywordPresetType>)
}

/** Add a [preset] to [presets][KeywordPresetAutoModerationRuleBuilder.presets]. */
public fun KeywordPresetAutoModerationRuleBuilder.preset(preset: AutoModerationRuleKeywordPresetType) {
    presets?.add(preset) ?: assignPresets(mutableListOf(preset))
}


/** An [AutoModerationRuleBuilder] for building rules with trigger type [MentionSpam]. */
@KordDsl
public sealed interface MentionSpamAutoModerationRuleBuilder :
    BlockMessageAutoModerationRuleBuilder,
    TimeoutAutoModerationRuleBuilder {

    override val triggerType: MentionSpam get() = MentionSpam

    /** Total number of unique role and user mentions allowed per message (maximum of 50). */
    public var mentionLimit: Int?

    /** Whether to automatically detect mention raids. */
    public var mentionRaidProtectionEnabled: Boolean?
}


/** An [AutoModerationRuleBuilder] for building rules with trigger type [MemberProfile]. */
public sealed interface MemberProfileAutoModerationRuleBuilder :
    FilteredKeywordsAutoModerationRuleBuilder,
    AllowedKeywordsAutoModerationRuleBuilder {

    override val triggerType: MemberProfile get() = MemberProfile
}

/** Add a [BlockMemberInteraction] action which will execute whenever the rule is triggered. */
public inline fun MemberProfileAutoModerationRuleBuilder.blockMemberInteraction(
    builder: BlockMemberInteractionAutoModerationActionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val action = BlockMemberInteractionAutoModerationActionBuilder().apply(builder)
    actions?.add(action) ?: assignActions(mutableListOf(action))
}


// -------- deprecated stuff --------

/** Add a [BlockMessage] action which will execute whenever the rule is triggered. */
@Deprecated(
    "Not all Auto Moderation Rules can have a 'BlockMessage' action (e.g. 'MemberProfile' rules can't), so this " +
        "extension function is deprecated for 'AutoModerationRuleBuilder'. Use the extension function on " +
        "'BlockMessageAutoModerationRuleBuilder' instead. The deprecation level will be raised to ERROR in 0.16.0, " +
        "to HIDDEN in 0.17.0, and this declaration will be removed in 0.18.0.",
    ReplaceWith(
        "(this as? BlockMessageAutoModerationRuleBuilder)?.blockMessage { builder() } ?: Unit",
        imports = [
            "dev.kord.rest.builder.automoderation.BlockMessageAutoModerationRuleBuilder",
            "dev.kord.rest.builder.automoderation.blockMessage", "kotlin.Unit",
        ],
    ),
    DeprecationLevel.WARNING,
)
public inline fun AutoModerationRuleBuilder.blockMessage(
    builder: BlockMessageAutoModerationActionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val action = BlockMessageAutoModerationActionBuilder().apply(builder)
    actions?.add(action) ?: assignActions(mutableListOf(action))
}

private const val MESSAGE = "This extension is now defined on 'FilteredKeywordsAutoModerationRuleBuilder'. The " +
    "declaration is kept for binary compatibility, it will be removed in 0.18.0."

@Deprecated(MESSAGE, level = DeprecationLevel.HIDDEN)
public fun KeywordAutoModerationRuleBuilder.keyword(keyword: String): Unit =
    (this as FilteredKeywordsAutoModerationRuleBuilder).keyword(keyword)

@Deprecated(MESSAGE, level = DeprecationLevel.HIDDEN)
public fun KeywordAutoModerationRuleBuilder.prefixKeyword(keyword: String): Unit =
    (this as FilteredKeywordsAutoModerationRuleBuilder).prefixKeyword(keyword)

@Deprecated(MESSAGE, level = DeprecationLevel.HIDDEN)
public fun KeywordAutoModerationRuleBuilder.suffixKeyword(keyword: String): Unit =
    (this as FilteredKeywordsAutoModerationRuleBuilder).suffixKeyword(keyword)

@Deprecated(MESSAGE, level = DeprecationLevel.HIDDEN)
public fun KeywordAutoModerationRuleBuilder.anywhereKeyword(keyword: String): Unit =
    (this as FilteredKeywordsAutoModerationRuleBuilder).anywhereKeyword(keyword)

@Deprecated(MESSAGE, level = DeprecationLevel.HIDDEN)
public fun KeywordAutoModerationRuleBuilder.regexPattern(pattern: String): Unit =
    (this as FilteredKeywordsAutoModerationRuleBuilder).regexPattern(pattern)
