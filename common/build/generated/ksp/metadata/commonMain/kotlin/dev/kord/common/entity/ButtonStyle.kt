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
 * Style of a [button][dev.kord.common.entity.ComponentType.Button].
 *
 * See [ButtonStyle]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/message-components#button-object-button-styles).
 */
@Serializable(with = ButtonStyle.Serializer::class)
public sealed class ButtonStyle(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ButtonStyle && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "ButtonStyle.Unknown(value=$value)"
            else "ButtonStyle.${this::class.simpleName}"

    /**
     * An unknown [ButtonStyle].
     *
     * This is used as a fallback for [ButtonStyle]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : ButtonStyle(value)

    /**
     * Blurple.
     */
    public object Primary : ButtonStyle(1)

    /**
     * Grey.
     */
    public object Secondary : ButtonStyle(2)

    /**
     * Green.
     */
    public object Success : ButtonStyle(3)

    /**
     * Red.
     */
    public object Danger : ButtonStyle(4)

    /**
     * Grey, navigates to a URL.
     */
    public object Link : ButtonStyle(5)

    internal object Serializer : KSerializer<ButtonStyle> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ButtonStyle", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ButtonStyle) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ButtonStyle = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [ButtonStyle]s.
         */
        public val entries: List<ButtonStyle> by lazy(mode = PUBLICATION) {
            listOf(
                Primary,
                Secondary,
                Success,
                Danger,
                Link,
            )
        }


        /**
         * Returns an instance of [ButtonStyle] with [ButtonStyle.value] equal to the specified
         * [value].
         */
        public fun from(`value`: Int): ButtonStyle = when (value) {
            1 -> Primary
            2 -> Secondary
            3 -> Success
            4 -> Danger
            5 -> Link
            else -> Unknown(value)
        }
    }
}
