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
 * Characterizes the type of content which can trigger the rule.
 *
 * See [AutoModerationRuleTriggerType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-trigger-types).
 */
@Serializable(with = AutoModerationRuleTriggerType.Serializer::class)
public sealed class AutoModerationRuleTriggerType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is AutoModerationRuleTriggerType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "AutoModerationRuleTriggerType.Unknown(value=$value)"
            else "AutoModerationRuleTriggerType.${this::class.simpleName}"

    /**
     * An unknown [AutoModerationRuleTriggerType].
     *
     * This is used as a fallback for [AutoModerationRuleTriggerType]s that haven't been added to
     * Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : AutoModerationRuleTriggerType(value)

    /**
     * Check if content contains words from a user defined list of keywords.
     */
    public object Keyword : AutoModerationRuleTriggerType(1)

    /**
     * Check if content represents generic spam.
     */
    public object Spam : AutoModerationRuleTriggerType(3)

    /**
     * Check if content contains words from internal pre-defined wordsets.
     */
    public object KeywordPreset : AutoModerationRuleTriggerType(4)

    /**
     * Check if content contains more unique mentions than allowed.
     */
    public object MentionSpam : AutoModerationRuleTriggerType(5)

    internal object Serializer : KSerializer<AutoModerationRuleTriggerType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationRuleTriggerType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: AutoModerationRuleTriggerType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): AutoModerationRuleTriggerType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [AutoModerationRuleTriggerType]s.
         */
        public val entries: List<AutoModerationRuleTriggerType> by lazy(mode = PUBLICATION) {
            listOf(
                Keyword,
                Spam,
                KeywordPreset,
                MentionSpam,
            )
        }

        /**
         * Returns an instance of [AutoModerationRuleTriggerType] with
         * [AutoModerationRuleTriggerType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): AutoModerationRuleTriggerType = when (value) {
            1 -> Keyword
            3 -> Spam
            4 -> KeywordPreset
            5 -> MentionSpam
            else -> Unknown(value)
        }
    }
}
