package dev.kord.core.behavior.automoderation

import dev.kord.common.entity.AutoModerationRuleTriggerType
import dev.kord.common.entity.AutoModerationRuleTriggerType.*
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.AutoModerationRuleData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.automoderation.*
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.automoderation.*
import java.util.Objects
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

// TODO documentation, missing vals/funs, factory methods, creation from unsafe

public interface AutoModerationRuleBehavior : KordEntity, Strategizable {

    /** The ID of the [Guild] which this rule belongs to. */
    public val guildId: Snowflake

    /** The behavior of the [Guild] which this rule belongs to. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /** The rule [trigger type][AutoModerationRuleTriggerType]. */
    public val triggerType: AutoModerationRuleTriggerType?

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationRuleBehavior
}

internal infix fun AutoModerationRuleBehavior.autoModerationRuleIsEqualTo(other: Any?) =
    this === other || (other is AutoModerationRuleBehavior && this.id == other.id && this.guildId == other.guildId)

internal fun AutoModerationRuleBehavior.hashAutoModerationRule() = Objects.hash(id, guildId)

public suspend inline fun AutoModerationRuleBehavior.edit(
    builder: UntypedAutoModerationRuleModifyBuilder.() -> Unit,
): AutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifyUntypedAutoModerationRule(guildId, ruleId = id, builder)
    return AutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}


public interface TypedAutoModerationRuleBehavior : AutoModerationRuleBehavior {
    override val triggerType: AutoModerationRuleTriggerType
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TypedAutoModerationRuleBehavior
}


public interface KeywordAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {
    override val triggerType: Keyword get() = Keyword
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordAutoModerationRuleBehavior
}

public suspend inline fun KeywordAutoModerationRuleBehavior.edit(
    builder: KeywordAutoModerationRuleModifyBuilder.() -> Unit,
): KeywordAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifyKeywordAutoModerationRule(guildId, ruleId = id, builder)
    return KeywordAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}


public interface HarmfulLinkAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {
    override val triggerType: HarmfulLink get() = HarmfulLink
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): HarmfulLinkAutoModerationRuleBehavior
}

public suspend inline fun HarmfulLinkAutoModerationRuleBehavior.edit(
    builder: HarmfulLinkAutoModerationRuleModifyBuilder.() -> Unit,
): HarmfulLinkAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifyHarmfulLinkAutoModerationRule(guildId, ruleId = id, builder)
    return HarmfulLinkAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}


public interface SpamAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {
    override val triggerType: Spam get() = Spam
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SpamAutoModerationRuleBehavior
}

public suspend inline fun SpamAutoModerationRuleBehavior.edit(
    builder: SpamAutoModerationRuleModifyBuilder.() -> Unit,
): SpamAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifySpamAutoModerationRule(guildId, ruleId = id, builder)
    return SpamAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}


public interface KeywordPresetAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {
    override val triggerType: KeywordPreset get() = KeywordPreset
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordPresetAutoModerationRuleBehavior
}

public suspend inline fun KeywordPresetAutoModerationRuleBehavior.edit(
    builder: KeywordPresetAutoModerationRuleModifyBuilder.() -> Unit,
): KeywordPresetAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifyKeywordPresetAutoModerationRule(guildId, ruleId = id, builder)
    return KeywordPresetAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}
