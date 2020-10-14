package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.WebhookData
import com.gitlab.kordlib.core.entity.Webhook
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.builder.webhook.WebhookCreateBuilder
import com.gitlab.kordlib.rest.json.request.BulkDeleteRequest
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.RestClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a Discord message channel associated to a [guild].
 */
interface GuildMessageChannelBehavior : GuildChannelBehavior, MessageChannelBehavior {

    /**
     * Requests to get all webhooks for this channel.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val webhooks: Flow<Webhook>
        get() = flow {
            for (response in kord.rest.webhook.getChannelWebhooks(id.value)) {
                val data = WebhookData.from(response)
                emit(Webhook(data, kord))
            }
        }

    /**
     * Requests to get the this behavior as a [GuildMessageChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a guild message channel.
     */
    override suspend fun asChannel(): GuildMessageChannel = super<GuildChannelBehavior>.asChannel() as GuildMessageChannel

    /**
     * Requests to get this behavior as a [GuildMessageChannel],
     * returns null if the channel isn't present or if the channel isn't a guild channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): GuildMessageChannel? =
            super<GuildChannelBehavior>.asChannelOrNull() as? GuildMessageChannel

    /**
     * Requests to bulk delete the [messages].
     * Messages older than 14 days will be deleted individually.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun bulkDelete(messages: Iterable<Snowflake>) {
        val daysLimit = Instant.now() - Duration.ofDays(14)
        //split up in bulk delete and manual delete
        // if message.timeMark + 14 days > now, then the message isn't 14 days old yet, and we can add it to the bulk delete
        // if message.timeMark + 14 days < now, then the message is more than 14 days old, and we'll have to manually delete them
        val (younger, older) = messages.partition { it.timeStamp.isAfter(daysLimit) }

        younger.map { it.value }.chunked(100).forEach {
            if (it.size < 2) kord.rest.channel.deleteMessage(id.value, it.first())
            else kord.rest.channel.bulkDelete(id.value, BulkDeleteRequest(it))
        }

        older.forEach { kord.rest.channel.deleteMessage(id.value, it.value) }
    }

    /**
     * Returns a new [GuildMessageChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildMessageChannelBehavior = GuildMessageChannelBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy) = object : GuildMessageChannelBehavior {
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
                return "GuildMessageChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
            }
        }
    }
}

/**
 * Requests to create a new webhook configured by the [builder].
 *
 * @return The created [Webhook] with the [Webhook.token] field present.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildMessageChannelBehavior.createWebhook(builder: WebhookCreateBuilder.() -> Unit): Webhook {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.webhook.createWebhook(id.value, builder)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}
