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
 * Indicates in what event context a rule should be checked.
 *
 * See [AutoModerationRuleEventType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-event-types).
 */
@Serializable(with = AutoModerationRuleEventType.Serializer::class)
public sealed class AutoModerationRuleEventType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is AutoModerationRuleEventType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "AutoModerationRuleEventType.Unknown(value=$value)"
            else "AutoModerationRuleEventType.${this::class.simpleName}"

    /**
     * An unknown [AutoModerationRuleEventType].
     *
     * This is used as a fallback for [AutoModerationRuleEventType]s that haven't been added to Kord
     * yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : AutoModerationRuleEventType(value)

    /**
     * When a member sends or edits a message in the guild.
     */
    public object MessageSend : AutoModerationRuleEventType(1)

    internal object Serializer : KSerializer<AutoModerationRuleEventType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationRuleEventType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: AutoModerationRuleEventType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): AutoModerationRuleEventType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [AutoModerationRuleEventType]s.
         */
        public val entries: List<AutoModerationRuleEventType> by lazy(mode = PUBLICATION) {
            listOf(
                MessageSend,
            )
        }


        /**
         * Returns an instance of [AutoModerationRuleEventType] with
         * [AutoModerationRuleEventType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): AutoModerationRuleEventType = when (value) {
            1 -> MessageSend
            else -> Unknown(value)
        }
    }
}
