package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.optional
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.interaction.InteractionApplicationCommandCallbackDataBuilder
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface InteractionBehavior : KordEntity, Strategizable {

    val applicationId: Snowflake
    val token: String
    val channelId: Snowflake?

    fun asGuildInteractionBehavior(): GuildInteractionBehavior =
        this as? GuildInteractionBehavior ?: error("This is not a GuildInteractionBehavior")

    fun asDMInteractionBehavior(): DMInteractionBehavior =
        this as? DMInteractionBehavior ?: error("This is not a DMInteractionBehavior")

    /**
     * Acknowledges an interaction.
     *
     * @param source weather to show the author's name and provided arguments of the command.
     * @return [InteractionResponseBehavior] which can be used to create follow-up message or edit the original response.
     */
    suspend fun acknowledge(source: Boolean = false): InteractionResponseBehavior {
        val type = if (source) InteractionResponseType.ACKWithSource
        else InteractionResponseType.Acknowledge
        val request = InteractionResponseCreateRequest(type)
        kord.rest.interaction.createInteractionResponse(id, token, request)
        return InteractionResponseBehavior(applicationId, token, kord)
    }
}

interface InteractionBehaviorWithChannel : InteractionBehavior {
    override val channelId: Snowflake

    suspend fun getChannelOrNull(): Channel? = supplier.getChannelOrNull(channelId)

    suspend fun getChannel(): Channel = supplier.getChannel(channelId)
}

@KordPreview
interface DMInteractionBehavior : InteractionBehaviorWithChannel {
    val userId: Snowflake

    suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    suspend fun getUser(): User? = supplier.getUser(userId)

    companion object {
        operator fun invoke(
            id: Snowflake,
            userId: Snowflake,
            token: String,
            applicationId: Snowflake,
            channelId: Snowflake,
            kord: Kord,
            strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ): DMInteractionBehavior = object : DMInteractionBehavior {
            override val userId: Snowflake
                get() = userId
            override val applicationId: Snowflake
                get() = applicationId
            override val token: String
                get() = token
            override val kord: Kord
                get() = kord
            override val id: Snowflake
                get() = id
            override val supplier: EntitySupplier
                get() = strategy.supply(kord)
            override val channelId: Snowflake
                get() = channelId

            override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable {
                return DMInteractionBehavior(id, userId, token, applicationId, channelId, kord, strategy)
            }

        }
    }
}

@KordPreview
interface GuildInteractionBehavior : InteractionBehavior {
    val guildId: Snowflake
    override val channelId: Snowflake

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    suspend fun getGuild(): Guild =
        supplier.getGuild(guildId)

    companion object {
        operator fun invoke(
            id: Snowflake,
            guildId: Snowflake,
            channelId: Snowflake,
            token: String,
            applicationId: Snowflake,
            kord: Kord,
            strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ): GuildInteractionBehavior =
            object : GuildInteractionBehavior {
                override val id: Snowflake
                    get() = id

                override val token: String
                    get() = token
                override val guildId: Snowflake
                    get() = guildId
                override val channelId: Snowflake
                    get() = channelId

                override val applicationId: Snowflake
                    get() = applicationId

                override val kord: Kord
                    get() = kord

                override val supplier: EntitySupplier
                    get() = strategy.supply(kord)

                override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable {
                    return GuildInteractionBehavior(id, guildId, channelId, token, applicationId, kord, strategy)
                }
            }
    }
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
    source: Boolean = false,
    builder: InteractionApplicationCommandCallbackDataBuilder.() -> Unit = {}
): InteractionResponseBehavior {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val type = if (source) InteractionResponseType.ChannelMessageWithSource
    else InteractionResponseType.ChannelMessage

    val data = InteractionApplicationCommandCallbackDataBuilder().apply(builder).build()
    val request = InteractionResponseCreateRequest(type, data.optional())
    kord.rest.interaction.createInteractionResponse(id, token, request)
    return InteractionResponseBehavior(applicationId, token, kord)

}