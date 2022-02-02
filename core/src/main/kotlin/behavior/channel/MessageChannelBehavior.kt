package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark

/**
 * The behavior of a Discord channel that can use messages.
 */
public interface MessageChannelBehavior : ChannelBehavior, Strategizable {

    /**
     * Requests to get this behavior as a [MessageChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a guild message channel.
     */
    override suspend fun asChannel(): MessageChannel = super.asChannel() as MessageChannel

    /**
     * Requests to get this behavior as a [MessageChannel],
     * returns null if the channel isn't present or if the channel isn't a guild channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): MessageChannel? = super.asChannelOrNull() as? MessageChannel

    /**
     * Retrieve the [MessageChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): MessageChannel = super.fetchChannel() as MessageChannel


    /**
     * Retrieve the [MessageChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [MessageChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): MessageChannel? = super.fetchChannelOrNull() as? MessageChannel

    /**
     * Requests to get all messages in this channel.
     *
     * Messages retrieved by this function will be emitted in chronological order (oldest -> newest).
     * Unrestricted consumption of the returned [Flow] is a potentially performance-intensive operation, it is thus
     * recommended limiting the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions
     * that limit the amount of messages requested.
     *
     * ```kotlin
     *  channel.getMessagesBefore(newer.id).takeWhile { it.id > older.id }
     * ```
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val messages: Flow<Message> get() = getMessagesAfter(Snowflake.min)

    /**
     * Requests to get the pinned messages in this channel.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val pinnedMessages: Flow<Message>
        get() = flow {
            val responses = kord.rest.channel.getChannelPins(id)

            for (response in responses) {
                val data = MessageData.from(response)
                emit(Message(data, kord))
            }
        }

    /**
     * Requests to create a message with only a [MessageCreateBuilder.content].
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun createMessage(content: String): Message = createMessage { this.content = content }

    /**
     * Requests to delete a message in this channel.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun deleteMessage(id: Snowflake, reason: String? = null): Unit =
        kord.rest.channel.deleteMessage(this.id, id, reason)

    /**
     * Requests to get all messages in this channel that were created **before** [messageId].
     *
     * Messages retrieved by this function will be emitted in reverse-chronological older (newest -> oldest).
     *
     * The flow may use paginated requests to supply messages, [limit] will limit the maximum number of messages
     * supplied and may optimize the batch size accordingly. `null` means no limit.
     *
     * Unrestricted consumption of the returned [Flow] is a potentially performance-intensive operation, it is thus
     * recommended limiting the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions
     * that limit the amount of messages requested.
     *
     * ```kotlin
     *  channel.getMessagesBefore(newer.id).takeWhile { it.id > older.id }
     * ```
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if a [limit] < 1 was supplied.
     */
    public fun getMessagesBefore(messageId: Snowflake, limit: Int? = null): Flow<Message> =
        supplier.getMessagesBefore(channelId = id, messageId = messageId, limit = limit)

    /**
     * Requests to get all messages in this channel that were created **after** [messageId].
     *
     * Messages retrieved by this function will be emitted in chronological older (oldest -> newest).
     *
     * The flow may use paginated requests to supply messages, [limit] will limit the maximum number of messages
     * supplied and may optimize the batch size accordingly. `null` means no limit.
     *
     * Unrestricted consumption of the returned [Flow] is a potentially performance-intensive operation, it is thus
     * recommended limiting the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions
     * that limit the amount of messages requested.
     *
     * ```kotlin
     *  channel.getMessagesAfter(older.id).takeWhile { it.id < newer.id }
     * ```
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if a [limit] < 1 was supplied.
     */
    public fun getMessagesAfter(messageId: Snowflake, limit: Int? = null): Flow<Message> =
        supplier.getMessagesAfter(channelId = id, messageId = messageId, limit = limit)

    /**
     * Requests to get [Message]s around (both older and newer) the [messageId].
     *
     * Messages retrieved by this function will be emitted in chronological older (oldest -> newest).
     *
     * Unlike [getMessagesAfter] and [getMessagesBefore], this flow can return **a maximum of 100 messages**.
     * As such, the accepted range of [limit] is reduced to 1..100.
     *
     * Supplied messages will be equally distributed before and after the [messageId].
     * The remaining message for an odd [limit] is undefined and may appear on either side or no side at all.
     *
     * If a message with the given [messageId] exists, the flow might also contain it, so it **could have one more
     * element than the given [limit]**.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * @throws IllegalArgumentException if the [limit] is outside the range of 1..100.
     */
    public fun getMessagesAround(messageId: Snowflake, limit: Int = 100): Flow<Message> =
        supplier.getMessagesAround(channelId = id, messageId = messageId, limit = limit)

    /**
     * Requests to get a message with the given [messageId].
     *
     * @throws RequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the message is null.
     */
    public suspend fun getMessage(messageId: Snowflake): Message = supplier.getMessage(id, messageId)

    /**
     * Requests to get a message with the given [messageId],
     * returns null if the message isn't present or is not part of this channel.
     *
     * @throws RequestException if something went wrong during the request.
     */
    public suspend fun getMessageOrNull(messageId: Snowflake): Message? = supplier.getMessageOrNull(id, messageId)

    /**
     * Requests to trigger the typing indicator for the bot in this channel.
     * The typing status will persist for 10 seconds or until the bot creates a message in the channel.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun type() {
        kord.rest.channel.triggerTypingIndicator(id)
    }

    /**
     * Requests to trigger the typing indicator for the bot in this channel.
     * The typing status will persist until the [mark] is reached.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun typeUntil(mark: TimeMark) {
        while (mark.hasNotPassedNow()) {
            type()
            delay(8.seconds) // bracing ourselves for some network delays
        }
    }

    /**
     * Requests to trigger the typing indicator for the bot in this channel.
     * The typing status will persist until the [instant] is reached.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun typeUntil(instant: Instant) {
        while (instant < Clock.System.now()) {
            type()
            delay(8.seconds) //bracing ourselves for some network delays
        }
    }

    /**
     * Returns a new [MessageChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MessageChannelBehavior =
        MessageChannelBehavior(id, kord, strategy)
}

public fun MessageChannelBehavior(
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): MessageChannelBehavior = object : MessageChannelBehavior {
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)


    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "MessageChannelBehavior(id=$id, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to create a message configured by the [builder].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun MessageChannelBehavior.createMessage(builder: UserMessageCreateBuilder.() -> Unit): Message {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.channel.createMessage(id, builder)
    val data = MessageData.from(response)

    return Message(data, kord)
}

/**
 * Requests to create a message with only an [embed][MessageCreateBuilder.embed].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun MessageChannelBehavior.createEmbed(block: EmbedBuilder.() -> Unit): Message {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return createMessage { embed(block) }
}

/**
 * Requests to trigger the typing indicator for the bot in this channel.
 * The typing status will be refreshed until the [block] has been completed.
 *
 * ```kotlin
 * channel.withTyping {
 *     delay(20.seconds.toLongMilliseconds()) //some very long task
 *     createMessage("done!")
 * }
 * ```
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun <T : MessageChannelBehavior> T.withTyping(block: T.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    var typing = true

    kord.launch(context = coroutineContext) {
        while (typing) {
            type()
            delay(8.seconds)
        }
    }

    try {
        block()
    } finally {
        typing = false
    }
}
