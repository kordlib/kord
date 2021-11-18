package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Interaction](https://discord.com/developers/docs/interactions/slash-commands#interaction)
 */

public interface InteractionBehavior : KordEntity, Strategizable {

    public val applicationId: Snowflake
    public val token: String
    public val channelId: Snowflake

    /**
     * The [MessageChannelBehavior] of the channel the command was executed in.
     */
    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)


    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)


    /**
     * Acknowledges an interaction ephemerally.
     *
     * @return [EphemeralInteractionResponseBehavior] Ephemeral acknowledgement of the interaction.
     */
    public suspend fun acknowledgeEphemeral(): EphemeralInteractionResponseBehavior {
         kord.rest.interaction.acknowledge(id, token, true)
        return EphemeralInteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges an interaction.
     *
     * @return [PublicInteractionResponseBehavior] public acknowledgement of an interaction.
     */
    public suspend fun acknowledgePublic(): PublicInteractionResponseBehavior {
        kord.rest.interaction.acknowledge(id, token)
        return PublicInteractionResponseBehavior(applicationId, token, kord)
    }


    public suspend fun getOriginalInteractionResponse(): Message? {
        return EntitySupplyStrategy.rest.supply(kord).getOriginalInteractionOrNull(applicationId, token)
    }


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): InteractionBehavior =
        InteractionBehavior(id, channelId, token, applicationId, kord, strategy)

}


/**
 * Acknowledges an interaction and responds with [PublicInteractionResponseBehavior].
 *
 * @param builder [InteractionResponseCreateBuilder] used to create a public response.
 * @return [PublicInteractionResponseBehavior] public response to the interaction.
 */

@OptIn(ExperimentalContracts::class)
public suspend inline fun InteractionBehavior.respondPublic(
    builder: InteractionResponseCreateBuilder.() -> Unit
): PublicInteractionResponseBehavior {

    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val request = InteractionResponseCreateBuilder().apply(builder).toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)
    return PublicInteractionResponseBehavior(applicationId, token, kord)

}


/**
 * Acknowledges an interaction and responds with [EphemeralInteractionResponseBehavior] with ephemeral flag.
 *
 * @param builder [InteractionResponseCreateBuilder] used to a create an ephemeral response.
 * @return [InteractionResponseBehavior] ephemeral response to the interaction.
 */

@OptIn(ExperimentalContracts::class)
public suspend inline fun InteractionBehavior.respondEphemeral(
    builder: InteractionResponseCreateBuilder.() -> Unit
): EphemeralInteractionResponseBehavior {

    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val builder = InteractionResponseCreateBuilder(true).apply(builder)
    val request = builder.toRequest()
    kord.rest.interaction.createInteractionResponse(id, token, request)
    return EphemeralInteractionResponseBehavior(applicationId, token, kord)

}

public fun InteractionBehavior(
    id: Snowflake,
    channelId: Snowflake,
    token: String,
    applicationId: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): InteractionBehavior = object : InteractionBehavior {
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


    override val supplier: EntitySupplier = strategy.supply(kord)

}
