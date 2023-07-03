// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection",
                "RedundantUnitReturnType"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordUnsafe
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
 * Convenience container of multiple [MessageFlags][MessageFlag] which can be combined into one.
 *
 * ## Creating a collection of message flags
 * You can create an [MessageFlags] object using the following methods
 * ```kotlin
 * // From flags
 * val flags1 = MessageFlags(MessageFlag.CrossPosted, MessageFlag.IsCrossPost)
 * // From an iterable
 * val flags2 = MessageFlags(listOf(MessageFlag.CrossPosted, MessageFlag.IsCrossPost))
 * // Using a builder
 * val flags3 = MessageFlags {
 *  +MessageFlag.CrossPosted
 *  -MessageFlag.IsCrossPost
 * }
 * ```
 *
 * ## Modifying existing flags
 * You can crate a modified copy of a [MessageFlags] instance using the [copy] method
 *
 * ```kotlin
 * flags.copy {
 *  +MessageFlag.CrossPosted
 * }
 * ```
 *
 * ## Mathematical operators
 * All [MessageFlags] objects can use +/- operators
 *
 * ```kotlin
 * val flags = MessageFlags(MessageFlag.CrossPosted)
 * val flags2 = flags + MessageFlag.IsCrossPost
 * val otherFlags = flags - MessageFlag.IsCrossPost
 * val flags3 = flags + otherFlags
 * ```
 *
 * ## Checking for a flag
 * You can use the [contains] operator to check whether a collection contains a specific flag
 * ```kotlin
 * val hasFlag = MessageFlag.CrossPosted in message.flags
 * val hasFlags = MessageFlag(MessageFlag.IsCrossPost, MessageFlag.IsCrossPost) in message.flags
 * ```
 *
 * ## Unknown flag
 *
 * Whenever a newly added flag has not been added to Kord yet it will get deserialized as
 * [MessageFlag.Unknown].
 * You can also use that to check for an yet unsupported flag
 * ```kotlin
 * val hasFlags = MessageFlag.Unknown(1 shl 69) in message.flags
 * ```
 * @see MessageFlag
 * @see MessageFlags.Builder
 * @property code numeric value of all [MessageFlags]s
 */
@Serializable(with = MessageFlags.Serializer::class)
public class MessageFlags(
    public val code: Int = 0,
) {
    public val values: Set<MessageFlag>
        get() = MessageFlag.entries.filter { it in this }.toSet()

    public operator fun contains(flag: MessageFlag): Boolean = this.code and flag.code == flag.code

    public operator fun contains(flags: MessageFlags): Boolean =
            this.code and flags.code == flags.code

    public operator fun plus(flag: MessageFlag): MessageFlags = MessageFlags(this.code or flag.code)

    public operator fun plus(flags: MessageFlags): MessageFlags =
            MessageFlags(this.code or flags.code)

    public operator fun minus(flag: MessageFlag): MessageFlags =
            MessageFlags(this.code and flag.code.inv())

    public operator fun minus(flags: MessageFlags): MessageFlags =
            MessageFlags(this.code and flags.code.inv())

    public override fun equals(other: Any?): Boolean = this === other ||
            (other is MessageFlags && this.code == other.code)

    public override fun hashCode(): Int = code.hashCode()

    public override fun toString(): String = "MessageFlags(values=$values)"

    public class Builder(
        private var code: Int = 0,
    ) {
        public operator fun MessageFlag.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun MessageFlags.unaryPlus(): Unit {
            this@Builder.code = this@Builder.code or this.code
        }

        public operator fun MessageFlag.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public operator fun MessageFlags.unaryMinus(): Unit {
            this@Builder.code = this@Builder.code and this.code.inv()
        }

        public fun flags(): MessageFlags = MessageFlags(code)
    }

    internal object Serializer : KSerializer<MessageFlags> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.MessageFlags", PrimitiveKind.INT)

        private val `delegate`: KSerializer<Int> = Int.serializer()

        public override fun serialize(encoder: Encoder, `value`: MessageFlags) =
                encoder.encodeSerializableValue(delegate, value.code)

        public override fun deserialize(decoder: Decoder) =
                MessageFlags(decoder.decodeSerializableValue(delegate))
    }
}

public inline fun MessageFlags(builder: MessageFlags.Builder.() -> Unit): MessageFlags {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    return MessageFlags.Builder().apply(builder).flags()
}

public fun MessageFlags(vararg flags: MessageFlag): MessageFlags = MessageFlags {
        flags.forEach { +it } }

public fun MessageFlags(vararg flags: MessageFlags): MessageFlags = MessageFlags {
        flags.forEach { +it } }

public fun MessageFlags(flags: Iterable<MessageFlag>): MessageFlags = MessageFlags {
        flags.forEach { +it } }

@JvmName("MessageFlags0")
public fun MessageFlags(flags: Iterable<MessageFlags>): MessageFlags = MessageFlags {
        flags.forEach { +it } }

public inline fun MessageFlags.copy(block: MessageFlags.Builder.() -> Unit): MessageFlags {
    contract { callsInPlace(block, EXACTLY_ONCE) }
    return MessageFlags.Builder(code).apply(block).flags()
}

/**
 * See [MessageFlag]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#message-object-message-flags).
 */
public sealed class MessageFlag(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is MessageFlag && this.code == other.code)

    public final override fun hashCode(): Int = code.hashCode()

    public final override fun toString(): String =
            "MessageFlag.${this::class.simpleName}(code=$code)"

    /**
     * An unknown [MessageFlag].
     *
     * This is used as a fallback for [MessageFlag]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        code: Int,
    ) : MessageFlag(code)

    /**
     * This message has been published to subscribed channels (via Channel Following).
     */
    public object CrossPosted : MessageFlag(1)

    /**
     * This message originated from a message in another channel (via Channel Following).
     */
    public object IsCrossPost : MessageFlag(2)

    /**
     * Do not include any embeds when serializing this message.
     */
    public object SuppressEmbeds : MessageFlag(4)

    /**
     * The source message for this crosspost has been deleted (via Channel Following).
     */
    public object SourceMessageDeleted : MessageFlag(8)

    /**
     * This message came from the urgent message system.
     */
    public object Urgent : MessageFlag(16)

    /**
     * This message has an associated thread, with the same id as the message.
     */
    public object HasThread : MessageFlag(32)

    /**
     * This message is only visible to the user who invoked the Interaction.
     */
    public object Ephemeral : MessageFlag(64)

    /**
     * This message is an Interaction Response and the bot is "thinking".
     */
    public object Loading : MessageFlag(128)

    /**
     * This message failed to mention some roles and add their members to the thread.
     */
    public object FailedToMentionSomeRolesInThread : MessageFlag(256)

    /**
     * This message will not trigger push and desktop notifications.
     */
    public object SuppressNotifications : MessageFlag(4_096)

    /**
     * This message is a voice message.
     */
    public object IsVoiceMessage : MessageFlag(8_192)

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
            )
        }

    }
}
