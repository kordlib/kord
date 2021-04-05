package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.TextChannelModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.patchTextChannel
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface TextChannelBehavior : GuildMessageChannelBehavior {

    /**
     * Requests to get the this behavior as a [TextChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a [TextChannel].
     */
    override suspend fun asChannel(): TextChannel = super.asChannel() as TextChannel

    /**
     * Requests to get this behavior as a [TextChannel],
     * returns null if the channel isn't present or if the channel isn't a [TextChannel].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun asChannelOrNull(): TextChannel? = super.asChannelOrNull() as? TextChannel


    /**
     * Returns a new [TextChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TextChannelBehavior = TextChannelBehavior(guildId, id, kord, strategy)

}

fun TextChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): TextChannelBehavior = object : TextChannelBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when(other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "TextChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier$supplier)"
    }
}


/**
 * Requests to edit this channel.
 *
 * @return The edited [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun TextChannelBehavior.edit(builder: TextChannelModifyBuilder.() -> Unit): TextChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.patchTextChannel(id, builder)
    val data = ChannelData.from(response)
    return Channel.from(data, kord) as TextChannel
}
