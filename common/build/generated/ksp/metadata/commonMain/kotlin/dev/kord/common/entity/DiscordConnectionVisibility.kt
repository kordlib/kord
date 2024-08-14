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
 * See [DiscordConnectionVisibility]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/user#connection-object-visibility-types).
 */
@Serializable(with = DiscordConnectionVisibility.Serializer::class)
public sealed class DiscordConnectionVisibility(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is DiscordConnectionVisibility && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "DiscordConnectionVisibility.Unknown(value=$value)"
            else "DiscordConnectionVisibility.${this::class.simpleName}"

    /**
     * An unknown [DiscordConnectionVisibility].
     *
     * This is used as a fallback for [DiscordConnectionVisibility]s that haven't been added to Kord
     * yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : DiscordConnectionVisibility(value)

    /**
     * Invisible to everyone except the user themselves.
     */
    public object None : DiscordConnectionVisibility(0)

    /**
     * Visible to everyone.
     */
    public object Everyone : DiscordConnectionVisibility(1)

    internal object Serializer : KSerializer<DiscordConnectionVisibility> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.DiscordConnectionVisibility",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: DiscordConnectionVisibility) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): DiscordConnectionVisibility =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [DiscordConnectionVisibility]s.
         */
        public val entries: List<DiscordConnectionVisibility> by lazy(mode = PUBLICATION) {
            listOf(
                None,
                Everyone,
            )
        }

        /**
         * Returns an instance of [DiscordConnectionVisibility] with
         * [DiscordConnectionVisibility.value] equal to the specified [value].
         */
        public fun from(`value`: Int): DiscordConnectionVisibility = when (value) {
            0 -> None
            1 -> Everyone
            else -> Unknown(value)
        }
    }
}
