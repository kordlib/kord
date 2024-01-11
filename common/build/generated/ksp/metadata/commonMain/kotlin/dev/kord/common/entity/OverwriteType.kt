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
 * See [OverwriteType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#overwrite-object-overwrite-structure).
 */
@Serializable(with = OverwriteType.Serializer::class)
public sealed class OverwriteType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is OverwriteType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "OverwriteType.Unknown(value=$value)"
            else "OverwriteType.${this::class.simpleName}"

    /**
     * An unknown [OverwriteType].
     *
     * This is used as a fallback for [OverwriteType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : OverwriteType(value)

    public object Role : OverwriteType(0)

    public object Member : OverwriteType(1)

    internal object Serializer : KSerializer<OverwriteType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.OverwriteType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: OverwriteType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): OverwriteType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [OverwriteType]s.
         */
        public val entries: List<OverwriteType> by lazy(mode = PUBLICATION) {
            listOf(
                Role,
                Member,
            )
        }


        /**
         * Returns an instance of [OverwriteType] with [OverwriteType.value] equal to the specified
         * [value].
         */
        public fun from(`value`: Int): OverwriteType = when (value) {
            0 -> Role
            1 -> Member
            else -> Unknown(value)
        }
    }
}
