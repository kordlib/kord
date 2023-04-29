package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.interaction.Interaction
import dev.kord.core.supplier.EntitySupplyStrategy

/** The behavior of an [Interaction]. */
public interface InteractionBehavior : KordEntity, Strategizable {

    /** The id of the application the interaction is for. */
    public val applicationId: Snowflake

    /** A continuation token for responding to the interaction. */
    public val token: String

    /** The id of the channel the interaction was sent from. */
    public val channelId: Snowflake?

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InteractionBehavior
}
