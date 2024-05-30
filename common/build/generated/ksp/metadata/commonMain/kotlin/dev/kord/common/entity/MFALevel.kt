// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection", "MemberVisibilityCanBePrivate"))

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
 * See [MFALevel]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-mfa-level).
 */
@Serializable(with = MFALevel.Serializer::class)
public sealed class MFALevel(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is MFALevel && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "MFALevel.Unknown(value=$value)"
            else "MFALevel.${this::class.simpleName}"

    /**
     * An unknown [MFALevel].
     *
     * This is used as a fallback for [MFALevel]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : MFALevel(value)

    /**
     * Guild has no MFA/2FA requirement for moderation actions.
     */
    public object None : MFALevel(0)

    /**
     * Guild has a 2FA requirement for moderation actions.
     */
    public object Elevated : MFALevel(1)

    internal object Serializer : KSerializer<MFALevel> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.MFALevel", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: MFALevel) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): MFALevel = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [MFALevel]s.
         */
        public val entries: List<MFALevel> by lazy(mode = PUBLICATION) {
            listOf(
                None,
                Elevated,
            )
        }


        /**
         * Returns an instance of [MFALevel] with [MFALevel.value] equal to the specified [value].
         */
        public fun from(`value`: Int): MFALevel = when (value) {
            0 -> None
            1 -> Elevated
            else -> Unknown(value)
        }
    }
}
