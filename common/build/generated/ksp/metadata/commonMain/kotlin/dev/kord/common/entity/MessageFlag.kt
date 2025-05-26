// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * See [MessageFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#message-object-message-flags).
 */
public sealed class MessageFlag(
    /**
     * The position of the bit that is set in this [MessageFlag]. This is always in 0..30.
     */
    public val shift: Int,
) {
    init {
        require(shift in 0..30) { """shift has to be in 0..30 but was $shift""" }
    }

    /**
     * The raw code used by Discord.
     */
    public val code: Int
        get() = 1 shl shift

    /**
     * Returns an instance of [MessageFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: MessageFlag): MessageFlags = MessageFlags(this.code or flag.code)

    /**
     * Returns an instance of [MessageFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: MessageFlags): MessageFlags =
            MessageFlags(this.code or flags.code)

    final override fun equals(other: Any?): Boolean = this === other ||
            (other is MessageFlag && this.shift == other.shift)

    final override fun hashCode(): Int = shift.hashCode()

    final override fun toString(): String = if (this is Unknown) "MessageFlag.Unknown(shift=$shift)"
            else "MessageFlag.${this::class.simpleName}"

    /**
     * An unknown [MessageFlag].
     *
     * This is used as a fallback for [MessageFlag]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        shift: Int,
    ) : MessageFlag(shift)

    /**
     * This message has been published to subscribed channels (via Channel Following).
     */
    public object CrossPosted : MessageFlag(0)

    /**
     * This message originated from a message in another channel (via Channel Following).
     */
    public object IsCrossPost : MessageFlag(1)

    /**
     * Do not include any embeds when serializing this message.
     */
    public object SuppressEmbeds : MessageFlag(2)

    /**
     * The source message for this crosspost has been deleted (via Channel Following).
     */
    public object SourceMessageDeleted : MessageFlag(3)

    /**
     * This message came from the urgent message system.
     */
    public object Urgent : MessageFlag(4)

    /**
     * This message has an associated thread, with the same id as the message.
     */
    public object HasThread : MessageFlag(5)

    /**
     * This message is only visible to the user who invoked the Interaction.
     */
    public object Ephemeral : MessageFlag(6)

    /**
     * This message is an Interaction Response and the bot is "thinking".
     */
    public object Loading : MessageFlag(7)

    /**
     * This message failed to mention some roles and add their members to the thread.
     */
    public object FailedToMentionSomeRolesInThread : MessageFlag(8)

    /**
     * This message will not trigger push and desktop notifications.
     */
    public object SuppressNotifications : MessageFlag(12)

    /**
     * This message is a voice message.
     */
    public object IsVoiceMessage : MessageFlag(13)

    /**
     * Allows you to create fully
     * [component](https://discord.com/developers/docs/components/overview)-driven messages.
     */
    public object IsComponentsV2 : MessageFlag(15)

    public companion object {
        /**
         * A [List] of all known [MessageFlag]s.
         */
        public val entries: List<MessageFlag> by lazy(mode = PUBLICATION) {
            listOf(
                CrossPosted,
                IsCrossPost,
                SuppressEmbeds,
                SourceMessageDeleted,
                Urgent,
                HasThread,
                Ephemeral,
                Loading,
                FailedToMentionSomeRolesInThread,
                SuppressNotifications,
                IsVoiceMessage,
                IsComponentsV2,
            )
        }

        /**
         * Returns an instance of [MessageFlag] with [MessageFlag.shift] equal to the specified
         * [shift].
         *
         * @throws IllegalArgumentException if [shift] is not in 0..30.
         */
        public fun fromShift(shift: Int): MessageFlag = when (shift) {
            0 -> CrossPosted
            1 -> IsCrossPost
            2 -> SuppressEmbeds
            3 -> SourceMessageDeleted
            4 -> Urgent
            5 -> HasThread
            6 -> Ephemeral
            7 -> Loading
            8 -> FailedToMentionSomeRolesInThread
            12 -> SuppressNotifications
            13 -> IsVoiceMessage
            15 -> IsComponentsV2
            else -> Unknown(shift)
        }
    }
}

/**
 * A collection of multiple [MessageFlag]s.
 *
 * ## Creating an instance of [MessageFlags]
 *
 * You can create an instance of [MessageFlags] using the following methods:
 * ```kotlin
 * // from individual MessageFlags
 * val messageFlags1 = MessageFlags(MessageFlag.CrossPosted, MessageFlag.IsCrossPost)
 *
 * // from an Iterable
 * val iterable: Iterable<MessageFlag> = TODO()
 * val messageFlags2 = MessageFlags(iterable)
 *
 * // using a builder
 * val messageFlags3 = MessageFlags {
 *     +messageFlags2
 *     +MessageFlag.CrossPosted
 *     -MessageFlag.IsCrossPost
 * }
 * ```
 *
 * ## Modifying an existing instance of [MessageFlags]
 *
 * You can create a modified copy of an existing instance of [MessageFlags] using the [copy] method:
 * ```kotlin
 * messageFlags.copy {
 *     +MessageFlag.CrossPosted
 * }
 * ```
 *
 * ## Mathematical operators
 *
 * All [MessageFlags] objects can use `+`/`-` operators:
 * ```kotlin
 * val messageFlags1 = messageFlags + MessageFlag.CrossPosted
 * val messageFlags2 = messageFlags - MessageFlag.IsCrossPost
 * val messageFlags3 = messageFlags1 + messageFlags2
 * ```
 *
 * ## Checking for [MessageFlag]s
 *
 * You can use the [contains] operator to check whether an instance of [MessageFlags] contains
 * specific [MessageFlag]s:
 * ```kotlin
 * val hasMessageFlag = MessageFlag.CrossPosted in messageFlags
 * val hasMessageFlags = MessageFlags(MessageFlag.CrossPosted, MessageFlag.IsCrossPost) in messageFlags
 * ```
 *
 * ## Unknown [MessageFlag]s
 *
 * Whenever [MessageFlag]s haven't been added to Kord yet, they will be deserialized as instances of
 * [MessageFlag.Unknown].
 *
 * You can also use [MessageFlag.fromShift] to check for [unknown][MessageFlag.Unknown]
 * [MessageFlag]s.
 * ```kotlin
 * val hasUnknownMessageFlag = MessageFlag.fromShift(23) in messageFlags
 * ```
 *
 * @see MessageFlag
 * @see MessageFlags.Builder
 */
@Serializable(with = MessageFlags.Serializer::class)
public class MessageFlags internal constructor(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    /**
     * A [Set] of all [MessageFlag]s contained in this instance of [MessageFlags].
     */
    public val values: Set<MessageFlag>
        get() = buildSet {
            var remaining = code
            var shift = 0
            while (remaining != 0) {
                if ((remaining and 1) != 0) add(MessageFlag.fromShift(shift))
                remaining = remaining ushr 1
                shift++
            }
        }

    /**
     * Checks if this instance of [MessageFlags] has all bits set that are set in [flag].
     */
    public operator fun contains(flag: MessageFlag): Boolean = this.code and flag.code == flag.code

    /**
     * Checks if this instance of [MessageFlags] has all bits set that are set in [flags].
     */
    public operator fun contains(flags: MessageFlags): Boolean =
            this.code and flags.code == flags.code

    /**
     * Returns an instance of [MessageFlags] that has all bits set that are set in `this` and
     * [flag].
     */
    public operator fun plus(flag: MessageFlag): MessageFlags = MessageFlags(this.code or flag.code)

    /**
     * Returns an instance of [MessageFlags] that has all bits set that are set in `this` and
     * [flags].
     */
    public operator fun plus(flags: MessageFlags): MessageFlags =
            MessageFlags(this.code or flags.code)

    /**
     * Returns an instance of [MessageFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flag].
     */
    public operator fun minus(flag: MessageFlag): MessageFlags =
            MessageFlags(this.code and flag.code.inv())

    /**
     * Returns an instance of [MessageFlags] that has all bits set that are set in `this` except the
     * bits that are set in [flags].
     */
    public operator fun minus(flags: MessageFlags): MessageFlags =
            MessageFlags(this.code and flags.code.inv())

    /**
     * Returns a copy of this instance of [MessageFlags] modified with [builder].
     */
    public inline fun copy(builder: Builder.() -> Unit): MessageFlags {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        return Builder(code).apply(builder).build()
    }

    override fun equals(other: Any?): Boolean = this === other ||
            (other is MessageFlags && this.code == other.code)

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = "MessageFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        /**
         * Sets all bits in the [Builder] that are set in this [MessageFlag].
         */
        public operator fun MessageFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Sets all bits in the [Builder] that are set in this [MessageFlags].
         */
        public operator fun MessageFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or this.code
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [MessageFlag].
         */
        public operator fun MessageFlag.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Unsets all bits in the [Builder] that are set in this [MessageFlags].
         */
        public operator fun MessageFlags.unaryMinus() {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        /**
         * Returns an instance of [MessageFlags] that has all bits set that are currently set in
         * this [Builder].
         */
        public fun build(): MessageFlags = MessageFlags(code)
    }

    internal object Serializer : KSerializer<MessageFlags> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.MessageFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        override fun serialize(encoder: Encoder, `value`: MessageFlags) {
            encoder.encodeSerializableValue(delegate, value.code)
        }

        override fun deserialize(decoder: Decoder): MessageFlags =
                MessageFlags(decoder.decodeSerializableValue(delegate))
    }
}

/**
 * Returns an instance of [MessageFlags] built with [MessageFlags.Builder].
 */
public inline fun MessageFlags(builder: MessageFlags.Builder.() -> Unit = {}): MessageFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return MessageFlags.Builder().apply(builder).build()
}

/**
 * Returns an instance of [MessageFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun MessageFlags(vararg flags: MessageFlag): MessageFlags = MessageFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [MessageFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun MessageFlags(vararg flags: MessageFlags): MessageFlags = MessageFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [MessageFlags] that has all bits set that are set in any element of
 * [flags].
 */
public fun MessageFlags(flags: Iterable<MessageFlag>): MessageFlags = MessageFlags {
    flags.forEach { +it }
}

/**
 * Returns an instance of [MessageFlags] that has all bits set that are set in any element of
 * [flags].
 */
@JvmName("MessageFlags0")
public fun MessageFlags(flags: Iterable<MessageFlags>): MessageFlags = MessageFlags {
    flags.forEach { +it }
}
