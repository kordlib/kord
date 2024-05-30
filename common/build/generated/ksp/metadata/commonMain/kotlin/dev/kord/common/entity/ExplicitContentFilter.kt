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
 * See [ExplicitContentFilter]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-explicit-content-filter-level).
 */
@Serializable(with = ExplicitContentFilter.Serializer::class)
public sealed class ExplicitContentFilter(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ExplicitContentFilter && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "ExplicitContentFilter.Unknown(value=$value)"
            else "ExplicitContentFilter.${this::class.simpleName}"

    /**
     * An unknown [ExplicitContentFilter].
     *
     * This is used as a fallback for [ExplicitContentFilter]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : ExplicitContentFilter(value)

    /**
     * Media content will not be scanned.
     */
    public object Disabled : ExplicitContentFilter(0)

    /**
     * Media content sent by members without roles will be scanned.
     */
    public object MembersWithoutRoles : ExplicitContentFilter(1)

    /**
     * Media content sent by all members will be scanned.
     */
    public object AllMembers : ExplicitContentFilter(2)

    internal object Serializer : KSerializer<ExplicitContentFilter> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ExplicitContentFilter",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ExplicitContentFilter) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ExplicitContentFilter =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [ExplicitContentFilter]s.
         */
        public val entries: List<ExplicitContentFilter> by lazy(mode = PUBLICATION) {
            listOf(
                Disabled,
                MembersWithoutRoles,
                AllMembers,
            )
        }


        /**
         * Returns an instance of [ExplicitContentFilter] with [ExplicitContentFilter.value] equal
         * to the specified [value].
         */
        public fun from(`value`: Int): ExplicitContentFilter = when (value) {
            0 -> Disabled
            1 -> MembersWithoutRoles
            2 -> AllMembers
            else -> Unknown(value)
        }
    }
}
