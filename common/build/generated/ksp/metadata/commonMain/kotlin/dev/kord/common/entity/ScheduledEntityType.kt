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
 * See [ScheduledEntityType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild-scheduled-event#guild-scheduled-event-object-guild-scheduled-event-entity-types).
 */
@Serializable(with = ScheduledEntityType.Serializer::class)
public sealed class ScheduledEntityType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ScheduledEntityType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            "ScheduledEntityType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [ScheduledEntityType].
     *
     * This is used as a fallback for [ScheduledEntityType]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : ScheduledEntityType(value)

    public object StageInstance : ScheduledEntityType(1)

    public object Voice : ScheduledEntityType(2)

    public object External : ScheduledEntityType(3)

    internal object Serializer : KSerializer<ScheduledEntityType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ScheduledEntityType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ScheduledEntityType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): ScheduledEntityType =
                when (val value = decoder.decodeInt()) {
            1 -> StageInstance
            2 -> Voice
            3 -> External
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [ScheduledEntityType]s.
         */
        public val entries: List<ScheduledEntityType> by lazy(mode = PUBLICATION) {
            listOf(
                StageInstance,
                Voice,
                External,
            )
        }

    }
}
