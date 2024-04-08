// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

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
 * See [ApplicationCommandType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-types).
 */
@Serializable(with = ApplicationCommandType.Serializer::class)
public sealed class ApplicationCommandType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationCommandType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "ApplicationCommandType.Unknown(value=$value)"
            else "ApplicationCommandType.${this::class.simpleName}"

    /**
     * An unknown [ApplicationCommandType].
     *
     * This is used as a fallback for [ApplicationCommandType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
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
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ApplicationCommandType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ApplicationCommandType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ApplicationCommandType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [ApplicationCommandType]s.
         */
        public val entries: List<ApplicationCommandType> by lazy(mode = PUBLICATION) {
            listOf(
                ChatInput,
                User,
                Message,
            )
        }


        /**
         * Returns an instance of [ApplicationCommandType] with [ApplicationCommandType.value] equal
         * to the specified [value].
         */
        public fun from(`value`: Int): ApplicationCommandType = when (value) {
            1 -> ChatInput
            2 -> User
            3 -> Message
            else -> Unknown(value)
        }
    }
}
