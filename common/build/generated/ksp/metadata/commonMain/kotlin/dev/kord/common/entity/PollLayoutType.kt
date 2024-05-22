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
 * See [PollLayoutType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/poll#layout-type).
 */
@Serializable(with = PollLayoutType.Serializer::class)
public sealed class PollLayoutType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is PollLayoutType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "PollLayoutType.Unknown(value=$value)"
            else "PollLayoutType.${this::class.simpleName}"

    /**
     * An unknown [PollLayoutType].
     *
     * This is used as a fallback for [PollLayoutType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : PollLayoutType(value)

    /**
     * The, uhm, default layout type.
     */
    public object DEFAULT : PollLayoutType(1)

    internal object Serializer : KSerializer<PollLayoutType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.PollLayoutType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: PollLayoutType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): PollLayoutType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [PollLayoutType]s.
         */
        public val entries: List<PollLayoutType> by lazy(mode = PUBLICATION) {
            listOf(
                DEFAULT,
            )
        }


        /**
         * Returns an instance of [PollLayoutType] with [PollLayoutType.value] equal to the
         * specified [value].
         */
        public fun from(`value`: Int): PollLayoutType = when (value) {
            1 -> DEFAULT
            else -> Unknown(value)
        }
    }
}
