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
 * Each [ApplicationRoleConnectionMetadataType] offers a comparison operation that allows guilds to
 * configure role requirements based on metadata values stored by the bot. Bots specify a 'metadata
 * value' for each user and guilds specify the required 'guild's configured value' within the guild
 * role settings.
 *
 * See [ApplicationRoleConnectionMetadataType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/application-role-connection-metadata#application-role-connection-metadata-object-application-role-connection-metadata-type).
 */
@Serializable(with = ApplicationRoleConnectionMetadataType.Serializer::class)
public sealed class ApplicationRoleConnectionMetadataType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationRoleConnectionMetadataType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "ApplicationRoleConnectionMetadataType.Unknown(value=$value)"
            else "ApplicationRoleConnectionMetadataType.${this::class.simpleName}"

    /**
     * An unknown [ApplicationRoleConnectionMetadataType].
     *
     * This is used as a fallback for [ApplicationRoleConnectionMetadataType]s that haven't been
     * added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : ApplicationRoleConnectionMetadataType(value)

    /**
     * The metadata value (`integer`) is less than or equal to the guild's configured value
     * (`integer`).
     */
    public object IntegerLessThanOrEqual : ApplicationRoleConnectionMetadataType(1)

    /**
     * The metadata value (`integer`) is greater than or equal to the guild's configured value
     * (`integer`).
     */
    public object IntegerGreaterThanOrEqual : ApplicationRoleConnectionMetadataType(2)

    /**
     * The metadata value (`integer`) is equal to the guild's configured value (`integer`).
     */
    public object IntegerEqual : ApplicationRoleConnectionMetadataType(3)

    /**
     * The metadata value (`integer`) is not equal to the guild's configured value (`integer`).
     */
    public object IntegerNotEqual : ApplicationRoleConnectionMetadataType(4)

    /**
     * The metadata value (`ISO8601 string`) is less than or equal to the guild's configured value
     * (`integer`; `days before current date`).
     */
    public object DateTimeLessThanOrEqual : ApplicationRoleConnectionMetadataType(5)

    /**
     * The metadata value (`ISO8601 string`) is greater than or equal to the guild's configured
     * value (`integer`; `days before current date`).
     */
    public object DateTimeGreaterThanOrEqual : ApplicationRoleConnectionMetadataType(6)

    /**
     * The metadata value (`integer`) is equal to the guild's configured value (`integer`; `1`).
     */
    public object BooleanEqual : ApplicationRoleConnectionMetadataType(7)

    /**
     * The metadata value (`integer`) is not equal to the guild's configured value (`integer`; `1`).
     */
    public object BooleanNotEqual : ApplicationRoleConnectionMetadataType(8)

    internal object Serializer : KSerializer<ApplicationRoleConnectionMetadataType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ApplicationRoleConnectionMetadataType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ApplicationRoleConnectionMetadataType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ApplicationRoleConnectionMetadataType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [ApplicationRoleConnectionMetadataType]s.
         */
        public val entries: List<ApplicationRoleConnectionMetadataType> by
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
         * Returns an instance of [ApplicationRoleConnectionMetadataType] with
         * [ApplicationRoleConnectionMetadataType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): ApplicationRoleConnectionMetadataType = when (value) {
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
