// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral"))

package dev.kord.common.entity

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.Suppress
import kotlin.collections.List
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DefaultMessageNotificationLevel.Serializer::class)
public sealed class DefaultMessageNotificationLevel(
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is DefaultMessageNotificationLevel && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    /**
     * An unknown [DefaultMessageNotificationLevel].
     *
     * This is used as a fallback for [DefaultMessageNotificationLevel]s that haven't been added to
     * Kord yet.
     */
    public class Unknown(
        `value`: Int,
    ) : DefaultMessageNotificationLevel(value)

    /**
     * Members will receive notifications for all messages by default.
     */
    public object AllMessages : DefaultMessageNotificationLevel(0)

    /**
     * Members will receive notifications only for messages that @mention them by default.
     */
    public object OnlyMentions : DefaultMessageNotificationLevel(1)

    internal object Serializer : KSerializer<DefaultMessageNotificationLevel> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.DefaultMessageNotificationLevel",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: DefaultMessageNotificationLevel) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            0 -> AllMessages
            1 -> OnlyMentions
            else -> Unknown(value)
        }
    }

    public companion object {
        public val entries: List<DefaultMessageNotificationLevel> by lazy(mode = PUBLICATION) {
            listOf(
                AllMessages,
                OnlyMentions,
            )
        }

    }
}
