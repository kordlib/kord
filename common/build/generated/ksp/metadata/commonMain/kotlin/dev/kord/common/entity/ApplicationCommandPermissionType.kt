// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection",
                "RedundantUnitReturnType"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordUnsafe
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
@OptIn(KordUnsafe::class)
public sealed class ApplicationCommandPermissionType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is ApplicationCommandPermissionType && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
            "ApplicationCommandPermissionType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [ApplicationCommandPermissionType].
     *
     * This is used as a fallback for [ApplicationCommandPermissionType]s that haven't been added to
     * Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        `value`: Int,
    ) : ApplicationCommandPermissionType(value)

    public object Role : ApplicationCommandPermissionType(1)

    public object User : ApplicationCommandPermissionType(2)

    public object Channel : ApplicationCommandPermissionType(3)

    internal object Serializer : KSerializer<ApplicationCommandPermissionType> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ApplicationCommandPermissionType",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: ApplicationCommandPermissionType) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> Role
            2 -> User
            3 -> Channel
            else -> Unknown(value)
        }
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

    }
}
