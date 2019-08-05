package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.message.EmbedBuilder
import com.gitlab.kordlib.core.`object`.builder.message.NewMessageBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * The behavior of a channel that can use messages.
 */
@ExperimentalCoroutinesApi
interface MessageChannelBehavior : ChannelBehavior {

    /**
     * Requests to create a message with only a [NewMessageBuilder.content].
     */
    suspend fun createMessage(content: String): Nothing /*Message*/ = TODO()

    /**
     * Requests to get all messages in this channel that were created *before* [messageId].
     *
     * Channels retrieved by this function will be emitted in reverse-chronological older (newest -> oldest).
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
    suspend fun getMessagesBefore(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Nothing /*Message*/> = TODO()

    /**
     * Requests to get all messages in this channel that were created *after* [messageId].
     *
     * Channels retrieved by this function will be emitted in chronological older (oldest -> newest).
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
    suspend fun getMessagesAfter(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Nothing /*Message*/> = TODO()

    /**
     * Requests to get messages around (both older and newer) the [messageId].
     *
     * Channels retrieved by this function will be emitted in chronological older (oldest -> newest).
     */
    suspend fun getMessagesAround(messageId: Snowflake): Flow<Nothing /*Message*/> = TODO()

    /**
     * Requests to get a message with the given [messageId].
     *
     * @return The Message with the given id in this Channel, or null if the message isn't present.
     */
    suspend fun getMessage(messageId: Snowflake): Nothing /*Message?*/ = TODO()

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
suspend inline fun MessageChannelBehavior.createMessage(block: NewMessageBuilder.() -> Unit): Nothing = TODO()

/**
 * Requests to create a message with only an [embed][NewMessageBuilder.embed].
 */
@ExperimentalCoroutinesApi
suspend inline fun MessageChannelBehavior.createEmbed(block: EmbedBuilder.() -> Unit): Nothing = TODO()
