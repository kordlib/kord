// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral"))

package dev.kord.common.entity

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ApplicationCommandType.Serializer::class)
public sealed class ApplicationCommandType(
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationCommandType && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
            "ApplicationCommandType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [ApplicationCommandType].
     *
     * This is used as a fallback for [ApplicationCommandType]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : ApplicationCommandType(value)

    /**
     * A text-based command that shows up when a user types `/`.
     */
    public object ChatInput : ApplicationCommandType(1)

    /**
     * A UI-based command that shows up when you right-click or tap on a user.
     */
    public object User : ApplicationCommandType(2)

    /**
     * A UI-based command that shows up when you right-click or tap on a message.
     */
    public object Message : ApplicationCommandType(3)

    internal object Serializer : KSerializer<ApplicationCommandType> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ApplicationCommandType",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: ApplicationCommandType) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> ChatInput
            2 -> User
            3 -> Message
            else -> Unknown(value)
        }
    }

    public companion object {
        public val entries: List<ApplicationCommandType> by lazy(mode = PUBLICATION) {
            listOf(
                ChatInput,
                User,
                Message,
            )
        }

    }
}
