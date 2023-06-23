package dev.kord.core.event.automoderation

import dev.kord.common.entity.Permission.ManageGuild
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.automoderation.AutoModerationRule
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [AutoModerationEvent] that is sent when an [AutoModerationRule] is configured (i.e.
 * [created][AutoModerationRuleCreateEvent], [updated][AutoModerationRuleUpdateEvent] or
 * [deleted][AutoModerationRuleDeleteEvent]).
 *
 * Events of this type are only sent to bot users which have the [ManageGuild] permission.
 */
public sealed interface AutoModerationRuleConfigurationEvent : AutoModerationEvent {

    /** The ID of the [rule]. */
    override val ruleId: Snowflake get() = rule.id

    /** The [AutoModerationRule] that was configured. */
    override val rule: AutoModerationRule

    override val guildId: Snowflake get() = rule.guildId

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationRuleConfigurationEvent
}

/**
 * An [AutoModerationRuleConfigurationEvent] that is sent when an [AutoModerationRule] is created.
 *
 * This event is only sent to bot users which have the [ManageGuild] permission.
 */
public class AutoModerationRuleCreateEvent(
    /** The [AutoModerationRule] that was created. */
    override val rule: AutoModerationRule,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : AutoModerationRuleConfigurationEvent {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationRuleCreateEvent =
        AutoModerationRuleCreateEvent(rule, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String = "AutoModerationRuleCreateEvent(rule=$rule, kord=$kord, shard=$shard, " +
            "customContext=$customContext, supplier=$supplier)"
}

/**
 * An [AutoModerationRuleConfigurationEvent] that is sent when an [AutoModerationRule] is updated.
 *
 * This event is only sent to bot users which have the [ManageGuild] permission.
 */
public class AutoModerationRuleUpdateEvent(
    /** The [AutoModerationRule] that was updated. */
    override val rule: AutoModerationRule,
    /** The [rule] as found in [cache][Kord.cache] before the update. */
    public val old: AutoModerationRule?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : AutoModerationRuleConfigurationEvent {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationRuleUpdateEvent =
        AutoModerationRuleUpdateEvent(rule, old, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String = "AutoModerationRuleUpdateEvent(rule=$rule, old=$old, kord=$kord, shard=$shard, " +
            "customContext=$customContext, supplier=$supplier)"
}

/**
 * An [AutoModerationRuleConfigurationEvent] that is sent when an [AutoModerationRule] is deleted.
 *
 * This event is only sent to bot users which have the [ManageGuild] permission.
 */
public class AutoModerationRuleDeleteEvent(
    /** The [AutoModerationRule] that was deleted. */
    override val rule: AutoModerationRule,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : AutoModerationRuleConfigurationEvent {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationRuleDeleteEvent =
        AutoModerationRuleDeleteEvent(rule, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String = "AutoModerationRuleDeleteEvent(rule=$rule, kord=$kord, shard=$shard, " +
            "customContext=$customContext, supplier=$supplier)"
}
