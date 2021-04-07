package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optional
import dev.kord.core.Kord
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.Channel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.interaction.InteractionApplicationCommandCallbackDataBuilder
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface InteractionBehavior : KordEntity, Strategizable {

    val applicationId: Snowflake
    val token: String
    val channelId: Snowflake

    /**
     * Acknowledges an interaction.
     *
     * @return [InteractionResponseBehavior] which can be used to create follow-up message or edit the original response.
     */
    suspend fun acknowledge(): InteractionResponseBehavior {

        val request = InteractionResponseCreateRequest(
            InteractionResponseType.DeferredChannelMessageWithSource,
        )
        kord.rest.interaction.createInteractionResponse(id, token, request)
        return InteractionResponseBehavior(applicationId, token, kord)
    }

    /**
     * Acknowledges an interaction.
     *
     * @param flags [MessageFlags] for the interaction response.
     * @return [InteractionResponseBehavior] which can be used to create follow-up message or edit the original response.
     */
    suspend fun acknowledge(flags: MessageFlags): InteractionResponseBehavior {

        val builder = InteractionApplicationCommandCallbackDataBuilder()
        builder.flags = flags

        val request = InteractionResponseCreateRequest(
            InteractionResponseType.DeferredChannelMessageWithSource,
            builder.build().optional()
        )
        kord.rest.interaction.createInteractionResponse(id, token, request)
        return InteractionResponseBehavior(applicationId, token, kord)
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
 * Acknowledges an interaction and responds with [InteractionResponseBehavior] built using [builder].
 *
 * @param source weather to show the author's name and provided arguments of the command.
 * @param builder [InteractionApplicationCommandCallbackDataBuilder] used to build a message.
 * @return [InteractionResponseBehavior] which can be used to create follow-up message or edit the original response.
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionBehavior.respond(
    builder: InteractionApplicationCommandCallbackDataBuilder.() -> Unit = {}
): InteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val data = InteractionApplicationCommandCallbackDataBuilder().apply(builder).build()
    val request = InteractionResponseCreateRequest(InteractionResponseType.ChannelMessageWithSource, data.optional())
    kord.rest.interaction.createInteractionResponse(id, token, request)
    return InteractionResponseBehavior(applicationId, token, kord)

}

@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionBehavior.acknowledge(builder: MessageFlags.Builder.() -> Unit): InteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val flags = MessageFlags.Builder().apply(builder).flags()
    return acknowledge(flags)
}