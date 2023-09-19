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
 * Premium types denote the level of premium a user has.
 *
 * See [UserPremium]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/user#user-object-premium-types).
 */
@Serializable(with = UserPremium.Serializer::class)
public sealed class UserPremium(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is UserPremium && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "UserPremium.Unknown(value=$value)"
            else "UserPremium.${this::class.simpleName}"

    /**
     * An unknown [UserPremium].
     *
     * This is used as a fallback for [UserPremium]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
        @Suppress(names = arrayOf("UNUSED_PARAMETER"))
        unused: Nothing?,
    ) : UserPremium(value) {
        @Deprecated(
            level = DeprecationLevel.ERROR,
            message = "Replaced by 'UserPremium.from()'.",
            replaceWith = ReplaceWith(expression = "UserPremium.from(value)", imports =
                        arrayOf("dev.kord.common.entity.UserPremium")),
        )
        public constructor(`value`: Int) : this(value, null)
    }

    public object None : UserPremium(0)

    public object NitroClassic : UserPremium(1)

    public object Nitro : UserPremium(2)

    public object NitroBasic : UserPremium(3)

    internal object Serializer : KSerializer<UserPremium> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.UserPremium", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: UserPremium) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): UserPremium = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [UserPremium]s.
         */
        public val entries: List<UserPremium> by lazy(mode = PUBLICATION) {
            listOf(
                None,
                NitroClassic,
                Nitro,
                NitroBasic,
            )
        }


        /**
         * Returns an instance of [UserPremium] with [UserPremium.value] equal to the specified
         * [value].
         */
        public fun from(`value`: Int): UserPremium = when (value) {
            0 -> None
            1 -> NitroClassic
            2 -> Nitro
            3 -> NitroBasic
            else -> Unknown(value, null)
        }
    }
}
