package dev.kord.core.behavior.channel

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.NewsChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.NewsChannelModifyBuilder
import dev.kord.rest.json.request.ChannelFollowRequest
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.patchNewsChannel
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a Discord News Channel associated to a guild.
 */
interface NewsChannelBehavior : GuildMessageChannelBehavior {

    /**
     * Requests to get the this behavior as a [NewsChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a [NewsChannel].
     */
    override suspend fun asChannel(): NewsChannel = super.asChannel() as NewsChannel

    /**
     * Requests to get this behavior as a [NewsChannel],
     * returns null if the channel isn't present or if the channel isn't a news channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): NewsChannel? = super.asChannelOrNull() as? NewsChannel


    /**
     * Requests to follow this channel, publishing cross posted messages to the [target] channel.
     *
     * This call requires the bot to have the [Permission.ManageWebhooks] permission.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    @KordPreview
    suspend fun follow(target: Snowflake) {
        kord.rest.channel.followNewsChannel(id, ChannelFollowRequest(webhookChannelId = target.asString))
    }

    /**
     * Returns a new [NewsChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): NewsChannelBehavior =
        NewsChannelBehavior(guildId, id, kord, strategy)

}

fun NewsChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): NewsChannelBehavior = object : NewsChannelBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "NewsChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to edit this channel.
 *
 * @return The edited [NewsChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun NewsChannelBehavior.edit(builder: NewsChannelModifyBuilder.() -> Unit): NewsChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.channel.patchNewsChannel(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as NewsChannel
}