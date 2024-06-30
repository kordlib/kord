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
 * See [EntitlementType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/monetization/entitlements#entitlement-object-entitlement-types).
 */
@Serializable(with = EntitlementType.Serializer::class)
public sealed class EntitlementType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is EntitlementType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "EntitlementType.Unknown(value=$value)"
            else "EntitlementType.${this::class.simpleName}"

    /**
     * An unknown [EntitlementType].
     *
     * This is used as a fallback for [EntitlementType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : EntitlementType(value)

    /**
     * Entitlement was purchased as an app subscription.
     */
    public object ApplicationSubscription : EntitlementType(8)

    internal object Serializer : KSerializer<EntitlementType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.EntitlementType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: EntitlementType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): EntitlementType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [EntitlementType]s.
         */
        public val entries: List<EntitlementType> by lazy(mode = PUBLICATION) {
            listOf(
                ApplicationSubscription,
            )
        }


        /**
         * Returns an instance of [EntitlementType] with [EntitlementType.value] equal to the
         * specified [value].
         */
        public fun from(`value`: Int): EntitlementType = when (value) {
            8 -> ApplicationSubscription
            else -> Unknown(value)
        }
    }
}
