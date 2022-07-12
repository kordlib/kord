package dev.kord.core.entity.automoderation

import dev.kord.common.entity.AutoModerationRuleEventType
import dev.kord.common.entity.AutoModerationRuleKeywordPresetType
import dev.kord.common.entity.AutoModerationRuleTriggerType
import dev.kord.common.entity.AutoModerationRuleTriggerType.*
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.automoderation.*
import dev.kord.core.behavior.automoderation.autoModerationRuleIsEqualTo
import dev.kord.core.behavior.automoderation.hashAutoModerationRule
import dev.kord.core.cache.data.AutoModerationRuleData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

// TODO missing vals, AutoModerationAction

public sealed class AutoModerationRule(
    public val data: AutoModerationRuleData,
    final override val kord: Kord,
    final override val supplier: EntitySupplier,
    expectedTriggerType: AutoModerationRuleTriggerType?,
) : TypedAutoModerationRuleBehavior {

    init {
        expectedTriggerType?.let {
            require(data.triggerType == it) { "Wrong trigger type, expected $it but got ${data.triggerType}" }
        }
    }

    final override val id: Snowflake get() = data.id

    final override val guildId: Snowflake get() = data.guildId

    /** The rule name. */
    public val name: String get() = data.name

    public val creatorId: Snowflake get() = data.creatorId
    public val creator: MemberBehavior get() = MemberBehavior(guildId, id = creatorId, kord)

    public val eventType: AutoModerationRuleEventType get() = data.eventType

    public val actions: List<Nothing> get() = TODO()

    /** Whether the rule is enabled. */
    public val isEnabled: Boolean get() = data.enabled

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationRule

    final override fun equals(other: Any?): Boolean = this autoModerationRuleIsEqualTo other
    final override fun hashCode(): Int = hashAutoModerationRule()
}

@PublishedApi
internal fun AutoModerationRule(
    data: AutoModerationRuleData,
    kord: Kord,
    supplier: EntitySupplier,
): AutoModerationRule = when (data.triggerType) {
    Keyword -> KeywordAutoModerationRule(data, kord, supplier)
    HarmfulLink -> HarmfulLinkAutoModerationRule(data, kord, supplier)
    Spam -> SpamAutoModerationRule(data, kord, supplier)
    KeywordPreset -> KeywordPresetAutoModerationRule(data, kord, supplier)
    is Unknown -> UnknownAutoModerationRule(data, kord, supplier)
}


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

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordAutoModerationRule =
        KeywordAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "KeywordAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}


public class HarmfulLinkAutoModerationRule(data: AutoModerationRuleData, kord: Kord, supplier: EntitySupplier) :
    AutoModerationRule(data, kord, supplier, expectedTriggerType = HarmfulLink),
    HarmfulLinkAutoModerationRuleBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): HarmfulLinkAutoModerationRule =
        HarmfulLinkAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "HarmfulLinkAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}


public class SpamAutoModerationRule(data: AutoModerationRuleData, kord: Kord, supplier: EntitySupplier) :
    AutoModerationRule(data, kord, supplier, expectedTriggerType = Spam),
    SpamAutoModerationRuleBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SpamAutoModerationRule =
        SpamAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "SpamAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}


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
    public val allowList: List<String> get() = data.triggerMetadata.allowList.orEmpty()

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordPresetAutoModerationRule =
        KeywordPresetAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "KeywordPresetAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}


public class UnknownAutoModerationRule(data: AutoModerationRuleData, kord: Kord, supplier: EntitySupplier) :
    AutoModerationRule(data, kord, supplier, expectedTriggerType = null) {

    init {
        require(data.triggerType is Unknown) { "Expected unknown trigger type but got ${data.triggerType}" }
    }

    override val triggerType: Unknown get() = data.triggerType as Unknown

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): UnknownAutoModerationRule =
        UnknownAutoModerationRule(data, kord, strategy.supply(kord))

    override fun toString(): String = "UnknownAutoModerationRule(data=$data, kord=$kord, supplier=$supplier)"
}
