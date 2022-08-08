package dev.kord.core.behavior.automoderation

import dev.kord.common.entity.AutoModerationRuleTriggerType
import dev.kord.common.entity.AutoModerationRuleTriggerType.*
import dev.kord.common.entity.Permission.ManageGuild
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.AutoModerationRuleData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.automoderation.*
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.automoderation.*
import dev.kord.rest.request.RestRequestException
import java.util.Objects
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

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
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    public suspend fun asAutoModerationRuleOrNull(): AutoModerationRule? =
        supplier.getAutoModerationRuleOrNull(guildId, ruleId = id)

    /**
     * Requests to get this behavior as an [AutoModerationRule].
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [AutoModerationRule] wasn't found.
     */
    public suspend fun asAutoModerationRule(): AutoModerationRule = supplier.getAutoModerationRule(guildId, ruleId = id)

    /**
     * Requests to get the [Guild] which this rule belongs to. Returns `null` if it wasn't found.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the [Guild] which this rule belongs to.
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [Guild] wasn't found.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

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

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String
}

internal fun AutoModerationRuleBehavior(
    guildId: Snowflake,
    ruleId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): AutoModerationRuleBehavior = object : AutoModerationRuleBehavior {
    override val guildId get() = guildId
    override val id get() = ruleId
    override val triggerType get() = null
    override val kord get() = kord
    override val supplier get() = supplier

    override fun withStrategy(strategy: EntitySupplyStrategy<*>) =
        AutoModerationRuleBehavior(guildId, ruleId, kord, strategy.supply(kord))

    override fun equals(other: Any?) = autoModerationRuleEquals(other)
    override fun hashCode() = autoModerationRuleHashCode()
    override fun toString() = "AutoModerationRuleBehavior(guildId=$guildId, id=$id, kord=$kord, supplier=$supplier)"
}

internal fun AutoModerationRuleBehavior.autoModerationRuleEquals(other: Any?) =
    this === other || (other is AutoModerationRuleBehavior && this.id == other.id && this.guildId == other.guildId)

internal fun AutoModerationRuleBehavior.autoModerationRuleHashCode() = Objects.hash(id, guildId)

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

internal fun TypedAutoModerationRuleBehavior(
    guildId: Snowflake,
    ruleId: Snowflake,
    triggerType: AutoModerationRuleTriggerType,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): TypedAutoModerationRuleBehavior = when (triggerType) {
    Keyword -> KeywordAutoModerationRuleBehavior(guildId, ruleId, kord, supplier)
    HarmfulLink -> HarmfulLinkAutoModerationRuleBehavior(guildId, ruleId, kord, supplier)
    Spam -> SpamAutoModerationRuleBehavior(guildId, ruleId, kord, supplier)
    KeywordPreset -> KeywordPresetAutoModerationRuleBehavior(guildId, ruleId, kord, supplier)
    MentionSpam -> MentionSpamAutoModerationRuleBehavior(guildId, ruleId, kord, supplier)
    is Unknown -> UnknownAutoModerationRuleBehavior(guildId, ruleId, triggerType, kord, supplier)
}


/** The behavior of a [KeywordAutoModerationRule]. */
public interface KeywordAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {

    override val triggerType: Keyword get() = Keyword

    /**
     * Requests to get this behavior as a [KeywordAutoModerationRule].
     * Returns `null` if it wasn't found or if the rule isn't a [KeywordAutoModerationRule].
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asAutoModerationRuleOrNull(): KeywordAutoModerationRule? =
        super.asAutoModerationRuleOrNull() as? KeywordAutoModerationRule

    /**
     * Requests to get this behavior as a [KeywordAutoModerationRule].
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [KeywordAutoModerationRule] wasn't found.
     * @throws ClassCastException if the rule isn't a [KeywordAutoModerationRule].
     */
    override suspend fun asAutoModerationRule(): KeywordAutoModerationRule =
        super.asAutoModerationRule() as KeywordAutoModerationRule

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordAutoModerationRuleBehavior
}

internal fun KeywordAutoModerationRuleBehavior(
    guildId: Snowflake,
    ruleId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): KeywordAutoModerationRuleBehavior = object : KeywordAutoModerationRuleBehavior {
    override val guildId get() = guildId
    override val id get() = ruleId
    override val kord get() = kord
    override val supplier get() = supplier

    override fun withStrategy(strategy: EntitySupplyStrategy<*>) =
        KeywordAutoModerationRuleBehavior(guildId, ruleId, kord, strategy.supply(kord))

    override fun equals(other: Any?) = autoModerationRuleEquals(other)
    override fun hashCode() = autoModerationRuleHashCode()
    override fun toString() =
        "KeywordAutoModerationRuleBehavior(guildId=$guildId, id=$id, kord=$kord, supplier=$supplier)"
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
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asAutoModerationRuleOrNull(): HarmfulLinkAutoModerationRule? =
        super.asAutoModerationRuleOrNull() as? HarmfulLinkAutoModerationRule

    /**
     * Requests to get this behavior as a [HarmfulLinkAutoModerationRule].
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [HarmfulLinkAutoModerationRule] wasn't found.
     * @throws ClassCastException if the rule isn't a [HarmfulLinkAutoModerationRule].
     */
    override suspend fun asAutoModerationRule(): HarmfulLinkAutoModerationRule =
        super.asAutoModerationRule() as HarmfulLinkAutoModerationRule

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): HarmfulLinkAutoModerationRuleBehavior
}

internal fun HarmfulLinkAutoModerationRuleBehavior(
    guildId: Snowflake,
    ruleId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): HarmfulLinkAutoModerationRuleBehavior = object : HarmfulLinkAutoModerationRuleBehavior {
    override val guildId get() = guildId
    override val id get() = ruleId
    override val kord get() = kord
    override val supplier get() = supplier

    override fun withStrategy(strategy: EntitySupplyStrategy<*>) =
        HarmfulLinkAutoModerationRuleBehavior(guildId, ruleId, kord, strategy.supply(kord))

    override fun equals(other: Any?) = autoModerationRuleEquals(other)
    override fun hashCode() = autoModerationRuleHashCode()
    override fun toString() =
        "HarmfulLinkAutoModerationRuleBehavior(guildId=$guildId, id=$id, kord=$kord, supplier=$supplier)"
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
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asAutoModerationRuleOrNull(): SpamAutoModerationRule? =
        super.asAutoModerationRuleOrNull() as? SpamAutoModerationRule

    /**
     * Requests to get this behavior as a [SpamAutoModerationRule].
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [SpamAutoModerationRule] wasn't found.
     * @throws ClassCastException if the rule isn't a [SpamAutoModerationRule].
     */
    override suspend fun asAutoModerationRule(): SpamAutoModerationRule =
        super.asAutoModerationRule() as SpamAutoModerationRule

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): SpamAutoModerationRuleBehavior
}

internal fun SpamAutoModerationRuleBehavior(
    guildId: Snowflake,
    ruleId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): SpamAutoModerationRuleBehavior = object : SpamAutoModerationRuleBehavior {
    override val guildId get() = guildId
    override val id get() = ruleId
    override val kord get() = kord
    override val supplier get() = supplier

    override fun withStrategy(strategy: EntitySupplyStrategy<*>) =
        SpamAutoModerationRuleBehavior(guildId, ruleId, kord, strategy.supply(kord))

    override fun equals(other: Any?) = autoModerationRuleEquals(other)
    override fun hashCode() = autoModerationRuleHashCode()
    override fun toString() =
        "SpamAutoModerationRuleBehavior(guildId=$guildId, id=$id, kord=$kord, supplier=$supplier)"
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
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asAutoModerationRuleOrNull(): KeywordPresetAutoModerationRule? =
        super.asAutoModerationRuleOrNull() as? KeywordPresetAutoModerationRule

    /**
     * Requests to get this behavior as a [KeywordPresetAutoModerationRule].
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [KeywordPresetAutoModerationRule] wasn't found.
     * @throws ClassCastException if the rule isn't a [KeywordPresetAutoModerationRule].
     */
    override suspend fun asAutoModerationRule(): KeywordPresetAutoModerationRule =
        super.asAutoModerationRule() as KeywordPresetAutoModerationRule

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): KeywordPresetAutoModerationRuleBehavior
}

internal fun KeywordPresetAutoModerationRuleBehavior(
    guildId: Snowflake,
    ruleId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): KeywordPresetAutoModerationRuleBehavior = object : KeywordPresetAutoModerationRuleBehavior {
    override val guildId get() = guildId
    override val id get() = ruleId
    override val kord get() = kord
    override val supplier get() = supplier

    override fun withStrategy(strategy: EntitySupplyStrategy<*>) =
        KeywordPresetAutoModerationRuleBehavior(guildId, ruleId, kord, strategy.supply(kord))

    override fun equals(other: Any?) = autoModerationRuleEquals(other)
    override fun hashCode() = autoModerationRuleHashCode()
    override fun toString() =
        "KeywordPresetAutoModerationRuleBehavior(guildId=$guildId, id=$id, kord=$kord, supplier=$supplier)"
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


/** The behavior of a [MentionSpamAutoModerationRule]. */
public interface MentionSpamAutoModerationRuleBehavior : TypedAutoModerationRuleBehavior {

    override val triggerType: MentionSpam get() = MentionSpam

    /**
     * Requests to get this behavior as a [MentionSpamAutoModerationRule].
     * Returns `null` if it wasn't found or if the rule isn't a [MentionSpamAutoModerationRule].
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    override suspend fun asAutoModerationRuleOrNull(): MentionSpamAutoModerationRule? =
        super.asAutoModerationRuleOrNull() as? MentionSpamAutoModerationRule

    /**
     * Requests to get this behavior as a [MentionSpamAutoModerationRule].
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [MentionSpamAutoModerationRule] wasn't found.
     * @throws ClassCastException if the rule isn't a [MentionSpamAutoModerationRule].
     */
    override suspend fun asAutoModerationRule(): MentionSpamAutoModerationRule =
        super.asAutoModerationRule() as MentionSpamAutoModerationRule

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MentionSpamAutoModerationRuleBehavior
}

internal fun MentionSpamAutoModerationRuleBehavior(
    guildId: Snowflake,
    ruleId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
): MentionSpamAutoModerationRuleBehavior = object : MentionSpamAutoModerationRuleBehavior {
    override val guildId get() = guildId
    override val id get() = ruleId
    override val kord get() = kord
    override val supplier get() = supplier

    override fun withStrategy(strategy: EntitySupplyStrategy<*>) =
        MentionSpamAutoModerationRuleBehavior(guildId, ruleId, kord, strategy.supply(kord))

    override fun equals(other: Any?) = autoModerationRuleEquals(other)
    override fun hashCode() = autoModerationRuleHashCode()
    override fun toString() =
        "MentionSpamAutoModerationRuleBehavior(guildId=$guildId, id=$id, kord=$kord, supplier=$supplier)"
}

/**
 * Requests to edit this [MentionSpamAutoModerationRule] and returns the edited rule.
 *
 * This requires the [ManageGuild] permission.
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun MentionSpamAutoModerationRuleBehavior.edit(
    builder: MentionSpamAutoModerationRuleModifyBuilder.() -> Unit,
): MentionSpamAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.modifyMentionSpamAutoModerationRule(guildId, ruleId = id, builder)
    return MentionSpamAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}


@Suppress("FunctionName")
internal fun UnknownAutoModerationRuleBehavior(
    guildId: Snowflake,
    ruleId: Snowflake,
    triggerType: Unknown,
    kord: Kord,
    supplier: EntitySupplier,
): TypedAutoModerationRuleBehavior = object : TypedAutoModerationRuleBehavior {
    override val guildId get() = guildId
    override val id get() = ruleId
    override val triggerType get() = triggerType
    override val kord get() = kord
    override val supplier get() = supplier

    override fun withStrategy(strategy: EntitySupplyStrategy<*>) =
        UnknownAutoModerationRuleBehavior(guildId, ruleId, triggerType, kord, strategy.supply(kord))

    override fun equals(other: Any?) = autoModerationRuleEquals(other)
    override fun hashCode() = autoModerationRuleHashCode()
    override fun toString() = "UnknownAutoModerationRuleBehavior(guildId=$guildId, id=$id, " +
            "triggerType=$triggerType, kord=$kord, supplier=$supplier)"
}
