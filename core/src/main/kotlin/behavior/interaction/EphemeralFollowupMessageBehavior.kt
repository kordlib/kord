package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The behavior of a [Discord Followup Message](https://discord.com/developers/docs/interactions/slash-commands#followup-messages)
 * This followup message is visible to *only* to the user who made the interaction.
 */

interface EphemeralFollowupMessageBehavior : FollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralFollowupMessageBehavior {
        return EphemeralFollowupMessageBehavior(id, applicationId, token, channelId, kord, strategy.supply(kord))
    }
}


fun EphemeralFollowupMessageBehavior(
    id: Snowflake,
    applicationId: Snowflake,
    token: String,
    channelId: Snowflake,
    kord: Kord,
    supplier: EntitySupplier
): EphemeralFollowupMessageBehavior = object : EphemeralFollowupMessageBehavior {
    override val applicationId: Snowflake
        get() = applicationId
    override val token: String
        get() = token
    override val channelId: Snowflake
        get() = channelId
    override val kord: Kord
        get() = kord
    override val id: Snowflake
        get() = id
    override val supplier: EntitySupplier
        get() = supplier

}
