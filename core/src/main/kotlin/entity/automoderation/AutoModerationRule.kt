package dev.kord.core.entity.automoderation

import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.common.entity.AutoModerationRuleKeywordPresetType
import dev.kord.common.entity.AutoModerationRuleTriggerType
import dev.kord.common.entity.AutoModerationRuleTriggerType.*
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.behavior.automoderation.*
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.cache.data.AutoModerationRuleData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of an [Auto Moderation Rule](https://discord.com/developers/docs/resources/auto-moderation).
 *
 * Auto Moderation is a feature which allows each [Guild] to set up rules that trigger based on some criteria. For
 * example, a rule can trigger whenever a message contains a specific keyword.
 *
 * Rules can be configured to automatically execute actions whenever they trigger. For example, if a user tries to send
 * a message which contains a certain keyword, a rule can trigger and block the message before it is sent.
 */
public sealed class AutoModerationRule(
    public val data: AutoModerationRuleData,
    final override val kord: Kord,
    final override val supplier: EntitySupplier,
    expectedTriggerType: AutoModerationRuleTriggerType?,
) : TypedAutoModerationRuleBehavior {

    init {
        if (expectedTriggerType == null) {
            require(data.triggerType is Unknown) { "Expected unknown trigger type but got ${data.triggerType}" }
        } else {
            require(data.triggerType == expectedTriggerType) {
                "Wrong trigger type, expected $expectedTriggerType but got ${data.triggerType}"
            }
        }
    }

    final override val id: Snowflake get() = data.id

    final override val guildId: Snowflake get() = data.guildId

    /** The rule name. */
    public val name: String get() = data.name

    /** The ID of the [Member] which first created this rule. */
    public val creatorId: Snowflake get() = data.creatorId

    /** The behavior of the [Member] which first created this rule. */
    public val creator: MemberBehavior get() = MemberBehavior(guildId, id = creatorId, kord)

    /** The rule [event type][AutoModerationRuleEventType]. */
    public val eventType: AutoModerationRuleEventType get() = data.eventType

    /** The actions which will execute when the rule is triggered. */
    public val actions: List<AutoModerationAction> get() = data.actions.map { AutoModerationAction(it, kord) }

    /** Whether the rule is enabled. */
    public val isEnabled: Boolean get() = data.enabled

    /** The IDs of the [Role]s that should not be affected by the rule. */
    public val exemptRoleIds: List<Snowflake> get() = data.exemptRoles

    /** The behaviors of the [Role]s that should not be affected by the rule. */
    public val exemptRoles: List<RoleBehavior> get() = data.exemptRoles.map { RoleBehavior(guildId, id = it, kord) }

    /** The IDs of the [GuildMessageChannel]s that should not be affected by the rule. */
    public val exemptChannelIds: List<Snowflake> get() = data.exemptChannels

    /** The behaviors of the [GuildMessageChannel]s that should not be affected by the rule. */
    public val exemptChannels: List<GuildMessageChannelBehavior>
        get() = data.exemptChannels.map { GuildMessageChannelBehavior(guildId, id = it, kord) }

    /**
     * Returns `this`.
     *
     * @suppress There is no need to call this function. Use `this` directly instead.
     */
    abstract override suspend fun asAutoModerationRuleOrNull(): AutoModerationRule

    /**
     * Returns `this`.
     *
     * @suppress There is no need to call this function. Use `this` directly instead.
     */
    abstract override suspend fun asAutoModerationRule(): AutoModerationRule

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationRule

    final override fun equals(other: Any?): Boolean = autoModerationRuleEquals(other)
    final override fun hashCode(): Int = autoModerationRuleHashCode()
}

@PublishedApi
internal fun AutoModerationRule(
    data: AutoModerationRuleData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): AutoModerationRule = when (data.triggerType) {
    Keyword -> KeywordAutoModerationRule(data, kord, supplier)
    Spam -> SpamAutoModerationRule(data, kord, supplier)
    KeywordPreset -> KeywordPresetAutoModerationRule(data, kord, supplier)
    MentionSpam -> MentionSpamAutoModerationRule(data, kord, supplier)
    is Unknown -> UnknownAutoModerationRule(data, kord, supplier)
}

/** An [AutoModerationRule] with trigger type [Keyword]. */
public class KeywordAutoModerationRule(data: AutoModerationRuleData, kord: Kord, supplier: EntitySupplier) :
    AutoModerationRule(data, kord, supplier, expectedTriggerType = Keyword),
    KeywordAutoModerationRuleBehavior {

    /**
     * Substrings which will be searched for in content.
     *
     * A keyword can be a phrase which contains multiple words. Wildcard symbols can be used to customize how each
     * keyword will be matched. See
     * [keyword matching strategies](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-matching-strategies).
     */
    public val keywords: List<String> get() = data.triggerMetadata.keywordFilter.orEmpty()

    override suspend fun asAutoModerationRuleOrNull(): KeywordAutoModerationRule = this
    override suspend fun asAutoModerationRule(): KeywordAutoModerationRule = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordAutoModerationRule =
        KeywordAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "KeywordAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}

/** An [AutoModerationRule] with trigger type [Spam]. */
public class SpamAutoModerationRule(data: AutoModerationRuleData, kord: Kord, supplier: EntitySupplier) :
    AutoModerationRule(data, kord, supplier, expectedTriggerType = Spam),
    SpamAutoModerationRuleBehavior {

    override suspend fun asAutoModerationRuleOrNull(): SpamAutoModerationRule = this
    override suspend fun asAutoModerationRule(): SpamAutoModerationRule = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SpamAutoModerationRule =
        SpamAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "SpamAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}

/** An [AutoModerationRule] with trigger type [KeywordPreset]. */
public class KeywordPresetAutoModerationRule(data: AutoModerationRuleData, kord: Kord, supplier: EntitySupplier) :
    AutoModerationRule(data, kord, supplier, expectedTriggerType = KeywordPreset),
    KeywordPresetAutoModerationRuleBehavior {

    /** The internally pre-defined wordsets which will be searched for in content. */
    public val presets: List<AutoModerationRuleKeywordPresetType> get() = data.triggerMetadata.presets.orEmpty()

    /**
     * Substrings which will be exempt from triggering the [presets].
     *
     * A keyword can be a phrase which contains multiple words.
     */
    public val allowedKeywords: List<String> get() = data.triggerMetadata.allowList.orEmpty()

    override suspend fun asAutoModerationRuleOrNull(): KeywordPresetAutoModerationRule = this
    override suspend fun asAutoModerationRule(): KeywordPresetAutoModerationRule = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordPresetAutoModerationRule =
        KeywordPresetAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "KeywordPresetAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}

/** An [AutoModerationRule] with trigger type [MentionSpam]. */
public class MentionSpamAutoModerationRule(data: AutoModerationRuleData, kord: Kord, supplier: EntitySupplier) :
    AutoModerationRule(data, kord, supplier, expectedTriggerType = MentionSpam),
    MentionSpamAutoModerationRuleBehavior {

    /** Total number of mentions (role & user) allowed per message. */
    public val mentionLimit: Int get() = data.triggerMetadata.mentionTotalLimit.value!!

    override suspend fun asAutoModerationRuleOrNull(): MentionSpamAutoModerationRule = this
    override suspend fun asAutoModerationRule(): MentionSpamAutoModerationRule = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MentionSpamAutoModerationRule =
        MentionSpamAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "MentionSpamAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}

/** An [AutoModerationRule] with trigger type [Unknown]. */
public class UnknownAutoModerationRule(data: AutoModerationRuleData, kord: Kord, supplier: EntitySupplier) :
    AutoModerationRule(data, kord, supplier, expectedTriggerType = null) {

    override val triggerType: Unknown get() = data.triggerType as Unknown

    override suspend fun asAutoModerationRuleOrNull(): UnknownAutoModerationRule = this
    override suspend fun asAutoModerationRule(): UnknownAutoModerationRule = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): UnknownAutoModerationRule =
        UnknownAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "UnknownAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}
