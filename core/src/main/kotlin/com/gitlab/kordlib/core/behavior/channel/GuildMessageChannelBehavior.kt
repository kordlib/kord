package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.WebhookData
import com.gitlab.kordlib.core.entity.Webhook
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.rest.builder.webhook.WebhookCreateBuilder
import com.gitlab.kordlib.rest.json.request.BulkDeleteRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.days

/**
 * The behavior of a Discord message channel associated to a [guild].
 */
interface GuildMessageChannelBehavior : GuildChannelBehavior, MessageChannelBehavior {

    /**
     * Requests to get all webhooks for this channel.
     */

    val webhooks: Flow<Webhook>
        get() = flow {
            for(response in kord.rest.webhook.getChannelWebhooks(id.value)) {
                val data = WebhookData.from(response)
                emit(Webhook(data,kord))
            }

        }


    /**
     * Requests to get the this behavior as a [GuildMessageChannel].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    override suspend fun asChannel(): GuildMessageChannel =  super<GuildChannelBehavior>.asChannel() as GuildMessageChannel

    /**
     * Requests to get this behavior as a [GuildMessageChannel].
     *
     * Entities will be fetched from the [RestClient][Kord.rest] directly, ignoring the [cache][Kord.cache].
     * Unless the currency of data is important, it is advised to use [asChannel] instead to reduce unneeded API calls.
     */
    override suspend fun requestChannel(): GuildMessageChannel = super<MessageChannelBehavior>.requestChannel() as GuildMessageChannel

    /**
     * Requests to bulk delete the [messages]. Sequentially deletes messages older than 14 days.
     */
    suspend fun bulkDelete(messages: Iterable<Snowflake>) {
        //split up in bulk delete and manual delete
        val messagesByRemoval = messages.groupBy { it.timeMark.plus(14.days).hasPassedNow() }

        val bulk = messagesByRemoval[false].orEmpty()
        when {
            bulk.size < 2 -> bulk.forEach { kord.rest.channel.deleteMessage(id.value, it.value) }
            else -> bulk.map { it.value }.chunked(100)
                    .map { BulkDeleteRequest(it) }
                    .forEach { kord.rest.channel.bulkDelete(id.value, it) }
        }

        val manual = messagesByRemoval[true].orEmpty()
        manual.forEach { kord.rest.channel.deleteMessage(id.value, it.value) }
    }

    //TODO 1.3.50 add delete messages? partially bulkdelete, manually delete older ones

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy) = object : GuildMessageChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy
        }
    }
}

/**
 * Requests to create a new webhook.
 *
 * @return The created [Webhook].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildMessageChannelBehavior.createWebhook(builder: WebhookCreateBuilder.() -> Unit): Webhook {
    val response = kord.rest.webhook.createWebhook(id.value, builder)
    val data = WebhookData.from(response)

    return Webhook(data, kord)
}
