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
 * Type of [DiscordRoleConnectionMetadata] values
 *
 * See [DiscordApplicationRoleConnectionMetadataRecordType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/application-role-connection-metadata#application-role-connection-metadata-object-application-role-connection-metadata-type).
 */
@Serializable(with = DiscordApplicationRoleConnectionMetadataRecordType.Serializer::class)
public sealed class DiscordApplicationRoleConnectionMetadataRecordType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is DiscordApplicationRoleConnectionMetadataRecordType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "DiscordApplicationRoleConnectionMetadataRecordType.Unknown(value=$value)"
            else "DiscordApplicationRoleConnectionMetadataRecordType.${this::class.simpleName}"

    /**
     * An unknown [DiscordApplicationRoleConnectionMetadataRecordType].
     *
     * This is used as a fallback for [DiscordApplicationRoleConnectionMetadataRecordType]s that
     * haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : DiscordApplicationRoleConnectionMetadataRecordType(value)

    /**
     * The metadata value (integer) is less than or equal to the guild's configured value (integer)
     */
    public object IntegerLessThanOrEqual : DiscordApplicationRoleConnectionMetadataRecordType(1)

    /**
     * The metadata value (integer) is greater than or equal to the guild's configured value
     * (integer)
     */
    public object IntegerGreaterThanOrEqual : DiscordApplicationRoleConnectionMetadataRecordType(2)

    /**
     * The metadata value (integer) is equal to the guild's configured value (integer)
     */
    public object IntegerEqual : DiscordApplicationRoleConnectionMetadataRecordType(3)

    /**
     * The metadata value (integer) is not equal to the guild's configured value (integer)
     */
    public object IntegerNotEqual : DiscordApplicationRoleConnectionMetadataRecordType(4)

    /**
     * The metadata value (ISO8601 string) is less than or equal to the guild's configured value
     * (integer; days before current date)
     */
    public object DateTimeLessThanOrEqual : DiscordApplicationRoleConnectionMetadataRecordType(5)

    /**
     * The metadata value (ISO8601 string) is greater than or equal to the guild's configured value
     * (integer; days before current date)
     */
    public object DateTimeGreaterThanOrEqual : DiscordApplicationRoleConnectionMetadataRecordType(6)

    /**
     * The metadata value (integer) is equal to the guild's configured value (integer; 1)
     */
    public object BooleanEqual : DiscordApplicationRoleConnectionMetadataRecordType(7)

    /**
     * The metadata value (integer) is not equal to the guild's configured value (integer; 1)
     */
    public object BooleanNotEqual : DiscordApplicationRoleConnectionMetadataRecordType(8)

    internal object Serializer : KSerializer<DiscordApplicationRoleConnectionMetadataRecordType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.DiscordApplicationRoleConnectionMetadataRecordType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder,
                `value`: DiscordApplicationRoleConnectionMetadataRecordType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder):
                DiscordApplicationRoleConnectionMetadataRecordType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [DiscordApplicationRoleConnectionMetadataRecordType]s.
         */
        public val entries: List<DiscordApplicationRoleConnectionMetadataRecordType> by
                lazy(mode = PUBLICATION) {
            listOf(
                IntegerLessThanOrEqual,
                IntegerGreaterThanOrEqual,
                IntegerEqual,
                IntegerNotEqual,
                DateTimeLessThanOrEqual,
                DateTimeGreaterThanOrEqual,
                BooleanEqual,
                BooleanNotEqual,
            )
        }


        /**
         * Returns an instance of [DiscordApplicationRoleConnectionMetadataRecordType] with
         * [DiscordApplicationRoleConnectionMetadataRecordType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): DiscordApplicationRoleConnectionMetadataRecordType =
                when (value) {
            1 -> IntegerLessThanOrEqual
            2 -> IntegerGreaterThanOrEqual
            3 -> IntegerEqual
            4 -> IntegerNotEqual
            5 -> DateTimeLessThanOrEqual
            6 -> DateTimeGreaterThanOrEqual
            7 -> BooleanEqual
            8 -> BooleanNotEqual
            else -> Unknown(value)
        }
    }
}
