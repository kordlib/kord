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
 * See [DefaultMessageNotificationLevel]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-object-default-message-notification-level).
 */
@Serializable(with = DefaultMessageNotificationLevel.Serializer::class)
public sealed class DefaultMessageNotificationLevel(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is DefaultMessageNotificationLevel && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "DefaultMessageNotificationLevel.Unknown(value=$value)"
            else "DefaultMessageNotificationLevel.${this::class.simpleName}"

    /**
     * An unknown [DefaultMessageNotificationLevel].
     *
     * This is used as a fallback for [DefaultMessageNotificationLevel]s that haven't been added to
     * Kord yet.
     */
    public class Unknown internal constructor(
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
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.DefaultMessageNotificationLevel",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: DefaultMessageNotificationLevel) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): DefaultMessageNotificationLevel =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [DefaultMessageNotificationLevel]s.
         */
        public val entries: List<DefaultMessageNotificationLevel> by lazy(mode = PUBLICATION) {
            listOf(
                AllMessages,
                OnlyMentions,
            )
        }


        /**
         * Returns an instance of [DefaultMessageNotificationLevel] with
         * [DefaultMessageNotificationLevel.value] equal to the specified [value].
         */
        public fun from(`value`: Int): DefaultMessageNotificationLevel = when (value) {
            0 -> AllMessages
            1 -> OnlyMentions
            else -> Unknown(value)
        }
    }
}
