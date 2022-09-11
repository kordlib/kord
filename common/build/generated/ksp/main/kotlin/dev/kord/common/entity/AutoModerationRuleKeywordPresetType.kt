// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral"))

package dev.kord.common.entity

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * An internally pre-defined wordset which will be searched for in content.
 */
@Serializable(with = AutoModerationRuleKeywordPresetType.Serializer::class)
public sealed class AutoModerationRuleKeywordPresetType(
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is AutoModerationRuleKeywordPresetType && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
            "AutoModerationRuleKeywordPresetType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [AutoModerationRuleKeywordPresetType].
     *
     * This is used as a fallback for [AutoModerationRuleKeywordPresetType]s that haven't been added
     * to Kord yet.
     */
    public class Unknown(
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
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationRuleKeywordPresetType",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder,
                `value`: AutoModerationRuleKeywordPresetType) = encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> Profanity
            2 -> SexualContent
            3 -> Slurs
            else -> Unknown(value)
        }
    }

    public companion object {
        public val entries: List<AutoModerationRuleKeywordPresetType> by lazy(mode = PUBLICATION) {
            listOf(
                Profanity,
                SexualContent,
                Slurs,
            )
        }

    }
}
