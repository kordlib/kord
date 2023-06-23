package dev.kord.core.event.automoderation

import dev.kord.common.entity.Permission.ManageGuild
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.automoderation.TypedAutoModerationRuleBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.automoderation.AutoModerationRule
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An [Event] that is associated with an [AutoModerationRule].
 *
 * Events of this type are only sent to bot users which have the [ManageGuild] permission.
 */
public sealed interface AutoModerationEvent : Event, Strategizable {

    /** The ID of the [AutoModerationRule] this event is associated with. */
    public val ruleId: Snowflake

    /** The behavior of the [AutoModerationRule] this event is associated with. */
    public val rule: TypedAutoModerationRuleBehavior

    /** The ID of the [Guild] this event is for. */
    public val guildId: Snowflake

    /** The behavior of the [Guild] this event is for. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the [Guild] this event is for. Returns `null` if it wasn't found.
     *
     * @throws RequestException if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the [Guild] this event is for.
     *
     * @throws RequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [Guild] wasn't found.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoModerationEvent

    override fun toString(): String
}
