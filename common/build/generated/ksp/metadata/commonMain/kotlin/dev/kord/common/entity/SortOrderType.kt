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
 * See [SortOrderType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#channel-object-sort-order-types).
 */
@Serializable(with = SortOrderType.Serializer::class)
public sealed class SortOrderType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SortOrderType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "SortOrderType.Unknown(value=$value)"
            else "SortOrderType.${this::class.simpleName}"

    /**
     * An unknown [SortOrderType].
     *
     * This is used as a fallback for [SortOrderType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : SortOrderType(value)

    /**
     * Sort forum posts by activity.
     */
    public object LatestActivity : SortOrderType(0)

    /**
     * Sort forum posts by creation time (from most recent to oldest).
     */
    public object CreationDate : SortOrderType(1)

    internal object Serializer : KSerializer<SortOrderType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.SortOrderType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: SortOrderType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): SortOrderType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [SortOrderType]s.
         */
        public val entries: List<SortOrderType> by lazy(mode = PUBLICATION) {
            listOf(
                LatestActivity,
                CreationDate,
            )
        }


        /**
         * Returns an instance of [SortOrderType] with [SortOrderType.value] equal to the specified
         * [value].
         */
        public fun from(`value`: Int): SortOrderType = when (value) {
            0 -> LatestActivity
            1 -> CreationDate
            else -> Unknown(value)
        }
    }
}
