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
 * An internally pre-defined wordset which will be searched for in content.
 *
 * See [AutoModerationRuleKeywordPresetType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-rule-object-keyword-preset-types).
 */
@Serializable(with = AutoModerationRuleKeywordPresetType.Serializer::class)
public sealed class AutoModerationRuleKeywordPresetType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is AutoModerationRuleKeywordPresetType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "AutoModerationRuleKeywordPresetType.Unknown(value=$value)"
            else "AutoModerationRuleKeywordPresetType.${this::class.simpleName}"

    /**
     * An unknown [AutoModerationRuleKeywordPresetType].
     *
     * This is used as a fallback for [AutoModerationRuleKeywordPresetType]s that haven't been added
     * to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : AutoModerationRuleKeywordPresetType(value)

    /**
     * Words that may be considered forms of swearing or cursing.
     */
    public object Profanity : AutoModerationRuleKeywordPresetType(1)

    /**
     * Words that refer to sexually explicit behavior or activity.
     */
    public object SexualContent : AutoModerationRuleKeywordPresetType(2)

    /**
     * Personal insults or words that may be considered hate speech.
     */
    public object Slurs : AutoModerationRuleKeywordPresetType(3)

    internal object Serializer : KSerializer<AutoModerationRuleKeywordPresetType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationRuleKeywordPresetType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: AutoModerationRuleKeywordPresetType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): AutoModerationRuleKeywordPresetType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [AutoModerationRuleKeywordPresetType]s.
         */
        public val entries: List<AutoModerationRuleKeywordPresetType> by lazy(mode = PUBLICATION) {
            listOf(
                Profanity,
                SexualContent,
                Slurs,
            )
        }

        /**
         * Returns an instance of [AutoModerationRuleKeywordPresetType] with
         * [AutoModerationRuleKeywordPresetType.value] equal to the specified [value].
         */
        public fun from(`value`: Int): AutoModerationRuleKeywordPresetType = when (value) {
            1 -> Profanity
            2 -> SexualContent
            3 -> Slurs
            else -> Unknown(value)
        }
    }
}
