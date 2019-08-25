package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.Pagination
import com.gitlab.kordlib.core.`object`.builder.message.EmbedBuilder
import com.gitlab.kordlib.core.`object`.builder.message.MessageCreateBuilder
import com.gitlab.kordlib.core.`object`.data.MessageData
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.rest.route.Position
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * The behavior of a Discord channel that can use messages.
 */
@ExperimentalCoroutinesApi
interface MessageChannelBehavior : ChannelBehavior {

    override suspend fun asChannel(): Channel {
        return super.asChannel()
    }

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
    val pinnedMessages: Flow<Message> get() = flow {
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
            Pagination.before(100, { it.id }) { position, size ->
                kord.rest.channel.getMessages(id.value, position, size)
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
            Pagination.after(100, { it.id }) { position, size ->
                kord.rest.channel.getMessages(id.value, position, size)
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
    suspend fun getMessage(messageId: Snowflake): Message? = kord.getMessage(id, messageId)

    /**
     * Requests to trigger the typing indicator for the bot in this channel. The typing status will persist for 10 seconds
     * or until the bot sends a message in the channel.
     */
    suspend fun type() {
        kord.rest.channel.triggerTypingIndicator(id.value)
    }

    //TODO 1.3.50 add fun typeUntil(mark: ClockMark): Unit

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) = object : MessageChannelBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

/**
 * Requests to create a message.
 */
@ExperimentalCoroutinesApi
suspend inline fun MessageChannelBehavior.createMessage(builder: MessageCreateBuilder.() -> Unit): Message {
    val request = MessageCreateBuilder().apply(builder).toRequest()

    val response = kord.rest.channel.createMessage(id.value, request)
    val data = MessageData.from(response)

    return Message(data, kord)
}

/**
 * Requests to create a message with only an [embed][MessageCreateBuilder.embed].
 */
@ExperimentalCoroutinesApi
suspend inline fun MessageChannelBehavior.createEmbed(block: EmbedBuilder.() -> Unit): Message = createMessage { embed(block) }
