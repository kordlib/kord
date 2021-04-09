package behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.EphemeralInteractionAcknowledgementBehavior
import dev.kord.core.behavior.interaction.EphemeralInteractionRespondBehavior
import dev.kord.core.behavior.interaction.InteractionResponseBehavior
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.Channel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.interaction.AcknowledgementResponseBuilder
import dev.kord.rest.builder.interaction.EphemeralInteractionResponseCreateBuilder
import dev.kord.rest.builder.interaction.PublicInteractionResponseCreateBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface InteractionBehavior : KordEntity, Strategizable {

    val applicationId: Snowflake
    val token: String
    val channelId: Snowflake

    /**
     * Acknowledges an interaction ephemerally.
     *
     * @return [EphemeralInteractionResponseBehavior] Ephemeral acknowledgement of the interaction.
     */
    suspend fun acknowledgeEphemeral(): EphemeralInteractionAcknowledgementBehavior {
        val request = AcknowledgementResponseBuilder(true).toRequest()
        kord.rest.interaction.createInteractionResponse(id, token, request)
        return EphemeralInteractionAcknowledgementBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges an interaction.
     *
     * @return [PublicInteractionResponseBehavior] public acknowledgement of an interaction.
     */
    suspend fun ackowledgePublic(): PublicInteractionResponseBehavior {
        val request = AcknowledgementResponseBuilder().toRequest()
        kord.rest.interaction.createInteractionResponse(id, token, request)
        return PublicInteractionResponseBehavior(applicationId, token, kord)
    }


    suspend fun getChannelOrNull(): Channel? = supplier.getChannelOrNull(channelId)


    suspend fun getChannel(): Channel = supplier.getChannel(channelId)


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InteractionBehavior =
        InteractionBehavior(id, channelId, token, applicationId, kord, strategy)

}

@KordPreview
fun InteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
) = object : InteractionBehavior {
    override val id: Snowflake
        get() = id

    override val token: String
        get() = token

    override val applicationId: Snowflake
        get() = applicationId

    override val kord: Kord
        get() = kord

    override val channelId: Snowflake
        get() = channelId


    override val supplier: EntitySupplier
        get() = strategy.supply(kord)

}

/**
 * Acknowledges an interaction and responds with [PublicInteractionResponseBehavior].
 *
 * @param builder [PublicInteractionResponseCreateBuilder] used to a create an public response.
 * @return [PublicInteractionResponseBehavior] public response to the interaction.
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionBehavior.respondPublic(
    builder: PublicInteractionResponseCreateBuilder.() -> Unit
): InteractionResponseBehavior {

    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = PublicInteractionResponseCreateBuilder().apply(builder).toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)
    return PublicInteractionResponseBehavior(applicationId, token, kord)

}


/**
 * Acknowledges an interaction and responds with [EphemeralInteractionResponseBehavior] with ephemeral flag.
 *
 * @param builder [EphemeralInteractionResponseCreateBuilder] used to a create an ephemeral response.
 * @return [EphemeralInteractionResponseBehavior] ephemeral response to the interaction.
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionBehavior.respondEphemeral(
    content: String,
    builder: EphemeralInteractionResponseCreateBuilder.() -> Unit = {}
): EphemeralInteractionRespondBehavior {

    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = EphemeralInteractionResponseCreateBuilder(content).apply(builder).toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)
    return EphemeralInteractionRespondBehavior(applicationId, token, kord)

}