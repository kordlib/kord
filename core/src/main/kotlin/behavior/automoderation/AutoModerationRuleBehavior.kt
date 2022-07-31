package dev.kord.core.behavior.automoderation

import dev.kord.common.entity.AutoModerationRuleTriggerType
import dev.kord.common.entity.AutoModerationRuleTriggerType.*
import dev.kord.common.entity.Permission.ManageGuild
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.AutoModerationRuleData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.automoderation.*
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.automoderation.*
import dev.kord.rest.request.RestRequestException
import java.util.Objects
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

// TODO factory methods, creation from unsafe

/** The behavior of an [AutoModerationRule]. */
public interface AutoModerationRuleBehavior : KordEntity, Strategizable {

    /** The ID of the [Guild] which this rule belongs to. */
    public val guildId: Snowflake

    /** The behavior of the [Guild] which this rule belongs to. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /** The rule [trigger type][AutoModerationRuleTriggerType]. */
    public val triggerType: AutoModerationRuleTriggerType?

    /**
     * Requests to get this behavior as an [AutoModerationRule]. Returns `null` if it wasn't found.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    public suspend fun asAutoModerationRuleOrNull(): AutoModerationRule? =
        supplier.getAutoModerationRuleOrNull(guildId, ruleId = id)

    /**
     * Requests to get this behavior as an [AutoModerationRule].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [AutoModerationRule] wasn't found.
     */
    public suspend fun asAutoModerationRule(): AutoModerationRule = supplier.getAutoModerationRule(guildId, ruleId = id)

    /**
     * Requests to delete this [AutoModerationRule].
     *
     * This requires the [ManageGuild] permission.
     *
     * @param reason the reason showing up in the audit log
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun delete(reason: String? = null) {
        kord.rest.autoModeration.deleteAutoModerationRule(guildId, ruleId = id, reason)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationRuleBehavior
}

internal infix fun AutoModerationRuleBehavior.autoModerationRuleIsEqualTo(other: Any?) =
    this === other || (other is AutoModerationRuleBehavior && this.id == other.id && this.guildId == other.guildId)

internal fun AutoModerationRuleBehavior.hashAutoModerationRule() = Objects.hash(id, guildId)

/**
 * Requests to edit this [AutoModerationRule] and returns the edited rule.
 *
 * This requires the [ManageGuild] permission.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun AutoModerationRuleBehavior.edit(
    builder: UntypedAutoModerationRuleModifyBuilder.() -> Unit,
): AutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifyUntypedAutoModerationRule(guildId, ruleId = id, builder)
    return AutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}


/** An [AutoModerationRuleBehavior] with a non-null [triggerType]. */
public interface TypedAutoModerationRuleBehavior : AutoModerationRuleBehavior {
    override val triggerType: AutoModerationRuleTriggerType
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TypedAutoModerationRuleBehavior
}


/** The behavior of a [KeywordAutoModerationRule]. */
public interface KeywordAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {

    override val triggerType: Keyword get() = Keyword

    /**
     * Requests to get this behavior as a [KeywordAutoModerationRule].
     * Returns `null` if it wasn't found or if the rule isn't a [KeywordAutoModerationRule].
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asAutoModerationRuleOrNull(): KeywordAutoModerationRule? =
        super.asAutoModerationRuleOrNull() as? KeywordAutoModerationRule

    /**
     * Requests to get this behavior as a [KeywordAutoModerationRule].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [KeywordAutoModerationRule] wasn't found.
     * @throws ClassCastException if the rule isn't a [KeywordAutoModerationRule].
     */
    override suspend fun asAutoModerationRule(): KeywordAutoModerationRule =
        super.asAutoModerationRule() as KeywordAutoModerationRule

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordAutoModerationRuleBehavior
}

/**
 * Requests to edit this [KeywordAutoModerationRule] and returns the edited rule.
 *
 * This requires the [ManageGuild] permission.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun KeywordAutoModerationRuleBehavior.edit(
    builder: KeywordAutoModerationRuleModifyBuilder.() -> Unit,
): KeywordAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifyKeywordAutoModerationRule(guildId, ruleId = id, builder)
    return KeywordAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}


/** The behavior of a [HarmfulLinkAutoModerationRule]. */
public interface HarmfulLinkAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {

    override val triggerType: HarmfulLink get() = HarmfulLink

    /**
     * Requests to get this behavior as a [HarmfulLinkAutoModerationRule].
     * Returns `null` if it wasn't found or if the rule isn't a [HarmfulLinkAutoModerationRule].
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asAutoModerationRuleOrNull(): HarmfulLinkAutoModerationRule? =
        super.asAutoModerationRuleOrNull() as? HarmfulLinkAutoModerationRule

    /**
     * Requests to get this behavior as a [HarmfulLinkAutoModerationRule].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [HarmfulLinkAutoModerationRule] wasn't found.
     * @throws ClassCastException if the rule isn't a [HarmfulLinkAutoModerationRule].
     */
    override suspend fun asAutoModerationRule(): HarmfulLinkAutoModerationRule =
        super.asAutoModerationRule() as HarmfulLinkAutoModerationRule

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): HarmfulLinkAutoModerationRuleBehavior
}

/**
 * Requests to edit this [HarmfulLinkAutoModerationRule] and returns the edited rule.
 *
 * This requires the [ManageGuild] permission.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun HarmfulLinkAutoModerationRuleBehavior.edit(
    builder: HarmfulLinkAutoModerationRuleModifyBuilder.() -> Unit,
): HarmfulLinkAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifyHarmfulLinkAutoModerationRule(guildId, ruleId = id, builder)
    return HarmfulLinkAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}


/** The behavior of a [SpamAutoModerationRule]. */
public interface SpamAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {

    override val triggerType: Spam get() = Spam

    /**
     * Requests to get this behavior as a [SpamAutoModerationRule].
     * Returns `null` if it wasn't found or if the rule isn't a [SpamAutoModerationRule].
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asAutoModerationRuleOrNull(): SpamAutoModerationRule? =
        super.asAutoModerationRuleOrNull() as? SpamAutoModerationRule

    /**
     * Requests to get this behavior as a [SpamAutoModerationRule].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [SpamAutoModerationRule] wasn't found.
     * @throws ClassCastException if the rule isn't a [SpamAutoModerationRule].
     */
    override suspend fun asAutoModerationRule(): SpamAutoModerationRule =
        super.asAutoModerationRule() as SpamAutoModerationRule

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SpamAutoModerationRuleBehavior
}

/**
 * Requests to edit this [SpamAutoModerationRule] and returns the edited rule.
 *
 * This requires the [ManageGuild] permission.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun SpamAutoModerationRuleBehavior.edit(
    builder: SpamAutoModerationRuleModifyBuilder.() -> Unit,
): SpamAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifySpamAutoModerationRule(guildId, ruleId = id, builder)
    return SpamAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}


/** The behavior of a [KeywordPresetAutoModerationRule]. */
public interface KeywordPresetAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {

    override val triggerType: KeywordPreset get() = KeywordPreset

    /**
     * Requests to get this behavior as a [KeywordPresetAutoModerationRule].
     * Returns `null` if it wasn't found or if the rule isn't a [KeywordPresetAutoModerationRule].
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asAutoModerationRuleOrNull(): KeywordPresetAutoModerationRule? =
        super.asAutoModerationRuleOrNull() as? KeywordPresetAutoModerationRule

    /**
     * Requests to get this behavior as a [KeywordPresetAutoModerationRule].
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [KeywordPresetAutoModerationRule] wasn't found.
     * @throws ClassCastException if the rule isn't a [KeywordPresetAutoModerationRule].
     */
    override suspend fun asAutoModerationRule(): KeywordPresetAutoModerationRule =
        super.asAutoModerationRule() as KeywordPresetAutoModerationRule

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordPresetAutoModerationRuleBehavior
}

/**
 * Requests to edit this [KeywordPresetAutoModerationRule] and returns the edited rule.
 *
 * This requires the [ManageGuild] permission.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun KeywordPresetAutoModerationRuleBehavior.edit(
    builder: KeywordPresetAutoModerationRuleModifyBuilder.() -> Unit,
): KeywordPresetAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifyKeywordPresetAutoModerationRule(guildId, ruleId = id, builder)
    return KeywordPresetAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}
