// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

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
 * See [IntegrationExpireBehavior]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#integration-object-integration-expire-behaviors).
 */
@Serializable(with = IntegrationExpireBehavior.Serializer::class)
public sealed class IntegrationExpireBehavior(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is IntegrationExpireBehavior && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            "IntegrationExpireBehavior.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [IntegrationExpireBehavior].
     *
     * This is used as a fallback for [IntegrationExpireBehavior]s that haven't been added to Kord
     * yet.
     */
    public class Unknown(
        `value`: Int,
    ) : IntegrationExpireBehavior(value)

    public object RemoveRole : IntegrationExpireBehavior(0)

    public object Kick : IntegrationExpireBehavior(1)

    internal object Serializer : KSerializer<IntegrationExpireBehavior> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.IntegrationExpireBehavior",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: IntegrationExpireBehavior) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): IntegrationExpireBehavior =
                when (val value = decoder.decodeInt()) {
            0 -> RemoveRole
            1 -> Kick
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [IntegrationExpireBehavior]s.
         */
        public val entries: List<IntegrationExpireBehavior> by lazy(mode = PUBLICATION) {
            listOf(
                RemoveRole,
                Kick,
            )
        }

    }
}
