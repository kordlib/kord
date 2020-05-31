package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.MessageData
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.paginateBackwards
import com.gitlab.kordlib.core.paginateForwards
import com.gitlab.kordlib.rest.builder.message.EmbedBuilder
import com.gitlab.kordlib.rest.builder.message.MessageCreateBuilder
import com.gitlab.kordlib.rest.route.Position
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.coroutines.coroutineContext
import kotlin.time.ClockMark
import kotlin.time.seconds

/**
 * The behavior of a Discord channel that can use messages.
 */
interface MessageChannelBehavior : ChannelBehavior, Strategizable {

    /**
     * Requests to get the this behavior as a [MessageChannel].
     *
     * Entities will be fetched from the [cache][Kord.cache] firstly and the [RestClient][Kord.rest] secondly.
     */
    override suspend fun asChannel(): MessageChannel =  super.asChannel() as MessageChannel

    /**
     * Requests to get all messages in this channel.
     *
     * Messages retrieved by this function will be emitted in chronological older (oldest -> newest).
     * Unrestricted consumption of the returned [Flow] is a potentially performance intensive operation, it is thus recommended
     * to limit the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions that limit the amount
     * of messages requested.
     *
     * ```kotlin
     *  channel.getMessagesBefore(newer.id).takeWhile { it.id > older.id }
     * ```
     */
    val messages: Flow<Message> get() = getMessagesAfter(Snowflake(0))

    /**
     * Requests to get the pinned messages in this channel.
     */
    val pinnedMessages: Flow<Message>
        get() = flow {
            val responses = kord.rest.channel.getChannelPins(id.value)

            for (response in responses) {
                val data = MessageData.from(response)
                emit(Message(data, kord))
            }
        }

    /**
     * Requests to create a message with only a [MessageCreateBuilder.content].
     */
    suspend fun createMessage(content: String): Message = createMessage { this.content = content }

    /**
     * Requests to delete a message in this channel.
     */
    suspend fun deleteMessage(id: Snowflake): Unit = kord.rest.channel.deleteMessage(this.id.value, id.value)

    /**
     * Requests to get all messages in this channel that were created *before* [messageId].
     *
     * Messages retrieved by this function will be emitted in reverse-chronological older (newest -> oldest).
     * Unrestricted consumption of the returned [Flow] is a potentially performance intensive operation, it is thus recommended
     * to limit the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions that limit the amount
     * of messages requested.
     *
     * ```kotlin
     *  channel.getMessagesBefore(newer.id).takeWhile { it.id > older.id }
     * ```
     *
     * @param limit a custom limit useful for requesting an amount of messages less than the default request limit (100). A value of
     * [Int.MAX_VALUE] means all messages before the [messageId].
     */
    fun getMessagesBefore(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message> =
            paginateBackwards(messageId, 100, idSelector = { it.id }) { position ->
                kord.rest.channel.getMessages(id.value, position, 100)
            }.map { MessageData.from(it) }.map { Message(it, kord) }

    /**
     * Requests to get all messages in this channel that were created *after* [messageId].
     *
     * Messages retrieved by this function will be emitted in chronological older (oldest -> newest).
     * Unrestricted consumption of the returned [Flow] is a potentially performance intensive operation, it is thus recommended
     * to limit the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions that limit the amount
     * of messages requested.
     *
     * ```kotlin
     *  channel.getMessagesAfter(older.id).takeWhile { it.id < newer.id }
     * ```
     *
     * @param limit a custom limit useful for requesting an amount of messages less than the default request limit (100). A value of
     * [Int.MAX_VALUE] means all messages after the [messageId].
     */
    fun getMessagesAfter(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Message> =
            paginateForwards(messageId, 100, idSelector = { it.id }) { position ->
                kord.rest.channel.getMessages(id.value, position, 100)
            }.map { MessageData.from(it) }.map { Message(it, kord) }

    /**
     * Requests to get messages around (both older and newer) the [messageId].
     *
     * Channels retrieved by this function will be emitted in chronological older (oldest -> newest).
     */
    fun getMessagesAround(messageId: Snowflake): Flow<Message> = flow {
        val responses = kord.rest.channel.getMessages(id.value, Position.Around(messageId.value))

        for (response in responses) {
            val data = MessageData.from(response)
            emit(Message(data, kord))
        }

    }

    /**
     * Requests to get a message with the given [messageId], if present.
     */
    suspend fun getMessage(messageId: Snowflake): Message = strategy.supply(kord).getMessage(id, messageId)

    suspend fun getMessageOrNull(messageId: Snowflake): Message? = strategy.supply(kord).getMessageOrNull(id, messageId)


    /**
     * Requests to trigger the typing indicator for the bot in this channel. The typing status will persist for 10 seconds
     * or until the bot sends a message in the channel.
     */
    suspend fun type() {
        kord.rest.channel.triggerTypingIndicator(id.value)
    }

    suspend fun typeUntil(mark: ClockMark) {
        while (mark.hasNotPassedNow()) {
            type()
            delay(8.seconds.toLongMilliseconds()) //bracing ourselves for some network delays
        }
    }

    suspend fun typeUntil(instant: Instant) {
        while (instant.isBefore(Instant.now())) {
            type()
            delay(8.seconds.toLongMilliseconds()) //bracing ourselves for some network delays
        }
    }

    /**
     * returns a new [MessageChannelBehavior] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */

    override fun withStrategy(strategy: EntitySupplyStrategy): MessageChannelBehavior = MessageChannelBehavior(id,kord,strategy)

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy) = object : MessageChannelBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy
        }
    }

}

/**
 * Requests to create a message.
 */
suspend inline fun MessageChannelBehavior.createMessage(builder: MessageCreateBuilder.() -> Unit): Message {
    val response = kord.rest.channel.createMessage(id.value, builder)
    val data = MessageData.from(response)

    return Message(data, kord)
}

/**
 * Requests to create a message with only an [embed][MessageCreateBuilder.embed].
 */
suspend inline fun MessageChannelBehavior.createEmbed(block: EmbedBuilder.() -> Unit): Message = createMessage { embed(block) }

/**
 * Requests to trigger the typing indicator for the bot in this channel.
 * The typing status will be refreshed until the `block` has been completed.
 *
 * ```kotlin
 * channel.withTyping {
 *     delay(20.seconds.toLongMilliseconds()) //some very long task
 *     createMessage("done!")
 * }
 * ```
 */
suspend inline fun <T : MessageChannelBehavior> T.withTyping(block: T.() -> Unit) {
    var typing = true

    kord.launch(context = coroutineContext) {
        while (typing) {
            type()
            delay(8.seconds.toLongMilliseconds())
        }
    }

    try {
        block()
    } finally {
        typing = false
    }
}
