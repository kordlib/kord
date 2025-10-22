// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

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
 *
 *
 * See [NameplatePalette]s in the [Discord Developer Documentation](https://discord.com/developers/docs/resources/user#nameplate-nameplate-structure).
 */
@Serializable(with = NameplatePalette.Serializer::class)
public sealed class NameplatePalette(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is NameplatePalette && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "NameplatePalette.Unknown(value=$value)" else "NameplatePalette.${this::class.simpleName}"

    /**
     * An unknown [NameplatePalette].
     *
     * This is used as a fallback for [NameplatePalette]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : NameplatePalette(value)

    public object CRIMSON : NameplatePalette("crimson")

    public object BERRY : NameplatePalette("berry")

    public object SKY : NameplatePalette("sky")

    public object TEAL : NameplatePalette("teal")

    public object FOREST : NameplatePalette("forest")

    public object BUBBLEGUM : NameplatePalette("bubble_gum")

    public object VIOLET : NameplatePalette("violet")

    public object COBALT : NameplatePalette("cobalt")

    public object CLOVER : NameplatePalette("clover")

    public object LEMON : NameplatePalette("lemon")

    public object WHITE : NameplatePalette("white")

    internal object Serializer : KSerializer<NameplatePalette> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.NameplatePalette", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: NameplatePalette) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): NameplatePalette = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [NameplatePalette]s.
         */
        public val entries: List<NameplatePalette> by lazy(mode = PUBLICATION) {
            listOf(
                CRIMSON,
                BERRY,
                SKY,
                TEAL,
                FOREST,
                BUBBLEGUM,
                VIOLET,
                COBALT,
                CLOVER,
                LEMON,
                WHITE,
            )
        }

        /**
         * Returns an instance of [NameplatePalette] with [NameplatePalette.value] equal to the specified [value].
         */
        public fun from(`value`: String): NameplatePalette = when (value) {
            "crimson" -> CRIMSON
            "berry" -> BERRY
            "sky" -> SKY
            "teal" -> TEAL
            "forest" -> FOREST
            "bubble_gum" -> BUBBLEGUM
            "violet" -> VIOLET
            "cobalt" -> COBALT
            "clover" -> CLOVER
            "lemon" -> LEMON
            "white" -> WHITE
            else -> Unknown(value)
        }
    }
}
