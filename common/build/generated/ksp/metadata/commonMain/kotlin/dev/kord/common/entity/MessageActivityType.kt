// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * See [MessageActivityType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#message-object-message-activity-types).
 */
@Serializable(with = MessageActivityType.Serializer::class)
public sealed class MessageActivityType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is MessageActivityType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            "MessageActivityType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [MessageActivityType].
     *
     * This is used as a fallback for [MessageActivityType]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : MessageActivityType(value)

    public object Join : MessageActivityType(1)

    public object Spectate : MessageActivityType(2)

    public object Listen : MessageActivityType(3)

    public object JoinRequest : MessageActivityType(5)

    internal object Serializer : KSerializer<MessageActivityType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.MessageActivityType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: MessageActivityType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): MessageActivityType =
                when (val value = decoder.decodeInt()) {
            1 -> Join
            2 -> Spectate
            3 -> Listen
            5 -> JoinRequest
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [MessageActivityType]s.
         */
        public val entries: List<MessageActivityType> by lazy(mode = PUBLICATION) {
            listOf(
                Join,
                Spectate,
                Listen,
                JoinRequest,
            )
        }

    }
}
