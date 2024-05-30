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
 * See [InviteTargetType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/invite#invite-object-invite-target-types).
 */
@Serializable(with = InviteTargetType.Serializer::class)
public sealed class InviteTargetType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is InviteTargetType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "InviteTargetType.Unknown(value=$value)"
            else "InviteTargetType.${this::class.simpleName}"

    /**
     * An unknown [InviteTargetType].
     *
     * This is used as a fallback for [InviteTargetType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : InviteTargetType(value)

    public object Stream : InviteTargetType(1)

    public object EmbeddedApplication : InviteTargetType(2)

    internal object Serializer : KSerializer<InviteTargetType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.InviteTargetType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: InviteTargetType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): InviteTargetType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [InviteTargetType]s.
         */
        public val entries: List<InviteTargetType> by lazy(mode = PUBLICATION) {
            listOf(
                Stream,
                EmbeddedApplication,
            )
        }


        /**
         * Returns an instance of [InviteTargetType] with [InviteTargetType.value] equal to the
         * specified [value].
         */
        public fun from(`value`: Int): InviteTargetType = when (value) {
            1 -> Stream
            2 -> EmbeddedApplication
            else -> Unknown(value)
        }
    }
}
