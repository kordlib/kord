package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.WebhookData
import dev.kord.core.entity.Webhook
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.webhook.WebhookCreateBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a non-thread Discord message channel associated to a [guild].
 *
 * 'Top' channels are those that do not require a parent channel to be created, and can be found at the top of the UI's hierarchy.
 *
 */
public interface TopGuildMessageChannelBehavior : CategorizableChannelBehavior, GuildMessageChannelBehavior {

    /**
     * Requests to get all webhooks for this channel.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val webhooks: Flow<Webhook>
        get() = flow {
            for (response in kord.rest.webhook.getChannelWebhooks(id)) {
                val data = WebhookData.from(response)
                emit(Webhook(data, kord))
            }
        }

    /**
     * Requests to get the this behavior as a [TopGuildMessageChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a guild message channel.
     */
    override suspend fun asChannel(): TopGuildMessageChannel =
        super<CategorizableChannelBehavior>.asChannel() as TopGuildMessageChannel

    /**
     * Requests to get this behavior as a [TopGuildMessageChannel],
     * returns null if the channel isn't present or if the channel isn't a guild channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): TopGuildMessageChannel? =
        super<CategorizableChannelBehavior>.asChannelOrNull() as? TopGuildMessageChannel

    /**
     * Retrieve the [TopGuildMessageChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): TopGuildMessageChannel =
        super<CategorizableChannelBehavior>.fetchChannel() as TopGuildMessageChannel


    /**
     * Retrieve the [TopGuildMessageChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [TopGuildMessageChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): TopGuildMessageChannel? =
        super<CategorizableChannelBehavior>.fetchChannelOrNull() as? TopGuildMessageChannel

    /**
     * Returns a new [TopGuildMessageChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TopGuildMessageChannelBehavior =
        TopGuildMessageChannelBehavior(guildId, id, kord, strategy)
}

internal fun TopGuildMessageChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
) = object : TopGuildMessageChannelBehavior {
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
        return "TopGuildMessageChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}


/**
 * Requests to create a new webhook configured by the [builder].
 *
 * @return The created [Webhook] with the [Webhook.token] field present.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun TopGuildMessageChannelBehavior.createWebhook(
    name: String,
    builder: WebhookCreateBuilder.() -> Unit = {}
): Webhook {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.webhook.createWebhook(id, name, builder)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}
