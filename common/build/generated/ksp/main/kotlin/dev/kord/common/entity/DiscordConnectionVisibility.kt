// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

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

@Serializable(with = DiscordConnectionVisibility.Serializer::class)
public sealed class DiscordConnectionVisibility(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is DiscordConnectionVisibility && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
            "DiscordConnectionVisibility.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [DiscordConnectionVisibility].
     *
     * This is used as a fallback for [DiscordConnectionVisibility]s that haven't been added to Kord
     * yet.
     */
    public class Unknown(
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
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.DiscordConnectionVisibility",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: DiscordConnectionVisibility) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            0 -> None
            1 -> Everyone
            else -> Unknown(value)
        }
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

    }
}
