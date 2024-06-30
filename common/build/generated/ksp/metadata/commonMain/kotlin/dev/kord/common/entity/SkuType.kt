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
 * See [SkuType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/monetization/skus#sku-object-sku-types).
 */
@Serializable(with = SkuType.Serializer::class)
public sealed class SkuType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is SkuType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "SkuType.Unknown(value=$value)"
            else "SkuType.${this::class.simpleName}"

    /**
     * An unknown [SkuType].
     *
     * This is used as a fallback for [SkuType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : SkuType(value)

    /**
     * Represents a recurring subscription.
     */
    public object Subscription : SkuType(5)

    /**
     * System-generated group for each [Subscription] SKU created.
     */
    public object SubscriptionGroup : SkuType(6)

    internal object Serializer : KSerializer<SkuType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.SkuType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: SkuType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): SkuType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [SkuType]s.
         */
        public val entries: List<SkuType> by lazy(mode = PUBLICATION) {
            listOf(
                Subscription,
                SubscriptionGroup,
            )
        }


        /**
         * Returns an instance of [SkuType] with [SkuType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): SkuType = when (value) {
            5 -> Subscription
            6 -> SubscriptionGroup
            else -> Unknown(value)
        }
    }
}
