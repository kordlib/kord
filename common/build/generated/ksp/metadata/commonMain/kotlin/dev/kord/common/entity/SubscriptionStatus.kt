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
 * See [SubscriptionStatus]es in the [Discord Developer Documentation](https://discord.com/developers/docs/resources/subscription#subscription-statuses).
 */
@Serializable(with = SubscriptionStatus.Serializer::class)
public sealed class SubscriptionStatus(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is SubscriptionStatus && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "SubscriptionStatus.Unknown(value=$value)" else "SubscriptionStatus.${this::class.simpleName}"

    /**
     * An unknown [SubscriptionStatus].
     *
     * This is used as a fallback for [SubscriptionStatus]es that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : SubscriptionStatus(value)

    /**
     * The subscription is active and scheduled to renew.
     */
    public object Active : SubscriptionStatus(0)

    /**
     * The subscription is active but will not renew.
     */
    public object Ending : SubscriptionStatus(1)

    /**
     * The subscription is inactive and not being charged.
     */
    public object Inactive : SubscriptionStatus(2)

    internal object Serializer : KSerializer<SubscriptionStatus> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.SubscriptionStatus", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: SubscriptionStatus) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): SubscriptionStatus = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [SubscriptionStatus]es.
         */
        public val entries: List<SubscriptionStatus> by lazy(mode = PUBLICATION) {
            listOf(
                Active,
                Ending,
                Inactive,
            )
        }

        /**
         * Returns an instance of [SubscriptionStatus] with [SubscriptionStatus.value] equal to the specified [value].
         */
        public fun from(`value`: Int): SubscriptionStatus = when (value) {
            0 -> Active
            1 -> Ending
            2 -> Inactive
            else -> Unknown(value)
        }
    }
}
