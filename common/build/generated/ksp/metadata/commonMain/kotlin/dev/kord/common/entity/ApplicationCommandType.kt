// THIS FILE IS AUTO-GENERATED BY GenerationProcessor, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection",
                "RedundantUnitReturnType"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordUnsafe
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
@OptIn(KordUnsafe::class)
public sealed class ApplicationCommandType(
    /**
     * The raw value used by Discord.
     */
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
    public class Unknown @KordUnsafe constructor(
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

    }
}
