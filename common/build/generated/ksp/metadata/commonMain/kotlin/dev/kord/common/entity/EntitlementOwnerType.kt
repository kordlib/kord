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
 * See [EntitlementOwnerType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/monetization/entitlements#create-test-entitlement).
 */
@Serializable(with = EntitlementOwnerType.Serializer::class)
public sealed class EntitlementOwnerType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is EntitlementOwnerType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "EntitlementOwnerType.Unknown(value=$value)"
            else "EntitlementOwnerType.${this::class.simpleName}"

    /**
     * An unknown [EntitlementOwnerType].
     *
     * This is used as a fallback for [EntitlementOwnerType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : EntitlementOwnerType(value)

    /**
     * Entitlement is owned by a guild.
     */
    public object Guild : EntitlementOwnerType(1)

    /**
     * Entitlement is owned by a user.
     */
    public object User : EntitlementOwnerType(2)

    internal object Serializer : KSerializer<EntitlementOwnerType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.EntitlementOwnerType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: EntitlementOwnerType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): EntitlementOwnerType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [EntitlementOwnerType]s.
         */
        public val entries: List<EntitlementOwnerType> by lazy(mode = PUBLICATION) {
            listOf(
                Guild,
                User,
            )
        }


        /**
         * Returns an instance of [EntitlementOwnerType] with [EntitlementOwnerType.value] equal to
         * the specified [value].
         */
        public fun from(`value`: Int): EntitlementOwnerType = when (value) {
            1 -> Guild
            2 -> User
            else -> Unknown(value)
        }
    }
}
