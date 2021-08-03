package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.modify.EphemeralFollowupMessageModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Followup Message](https://discord.com/developers/docs/interactions/slash-commands#followup-messages)
 * This followup message is visible to *only* to the user who made the interaction.
 */
@KordPreview
interface EphemeralFollowupMessageBehavior : FollowupMessageBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): EphemeralFollowupMessageBehavior {
        return EphemeralFollowupMessageBehavior(id, applicationId, token, channelId, kord, strategy.supply(kord))
    }
}

/**
 * Requests to edit this followup message.
 *
 * @return The edited [PublicFollowupMessage] of the interaction response.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun EphemeralFollowupMessageBehavior.edit(builder: EphemeralFollowupMessageModifyBuilder.() -> Unit): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = EphemeralFollowupMessageModifyBuilder().apply(builder)
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder.toRequest())
    return EphemeralFollowupMessage(Message(response.toData(), kord), applicationId, token, kord)
}

@KordPreview
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
