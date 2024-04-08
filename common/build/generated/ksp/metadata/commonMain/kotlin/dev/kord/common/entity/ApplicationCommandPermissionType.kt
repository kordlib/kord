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
 * See [ApplicationCommandPermissionType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/interactions/application-commands#application-command-permissions-object-application-command-permission-type).
 */
@Serializable(with = ApplicationCommandPermissionType.Serializer::class)
public sealed class ApplicationCommandPermissionType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationCommandPermissionType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "ApplicationCommandPermissionType.Unknown(value=$value)"
            else "ApplicationCommandPermissionType.${this::class.simpleName}"

    /**
     * An unknown [ApplicationCommandPermissionType].
     *
     * This is used as a fallback for [ApplicationCommandPermissionType]s that haven't been added to
     * Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : ApplicationCommandPermissionType(value)

    public object Role : ApplicationCommandPermissionType(1)

    public object User : ApplicationCommandPermissionType(2)

    public object Channel : ApplicationCommandPermissionType(3)

    internal object Serializer : KSerializer<ApplicationCommandPermissionType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ApplicationCommandPermissionType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ApplicationCommandPermissionType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ApplicationCommandPermissionType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [ApplicationCommandPermissionType]s.
         */
        public val entries: List<ApplicationCommandPermissionType> by lazy(mode = PUBLICATION) {
            listOf(
                Role,
                User,
                Channel,
            )
        }


        /**
         * Returns an instance of [ApplicationCommandPermissionType] with
         * [ApplicationCommandPermissionType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): ApplicationCommandPermissionType = when (value) {
            1 -> Role
            2 -> User
            3 -> Channel
            else -> Unknown(value)
        }
    }
}
