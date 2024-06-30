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
 * See [OnboardingPromptType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-onboarding-object-prompt-types).
 */
@Serializable(with = OnboardingPromptType.Serializer::class)
public sealed class OnboardingPromptType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is OnboardingPromptType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "OnboardingPromptType.Unknown(value=$value)"
            else "OnboardingPromptType.${this::class.simpleName}"

    /**
     * An unknown [OnboardingPromptType].
     *
     * This is used as a fallback for [OnboardingPromptType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : OnboardingPromptType(value)

    public object MultipleChoice : OnboardingPromptType(0)

    public object Dropdown : OnboardingPromptType(1)

    internal object Serializer : KSerializer<OnboardingPromptType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.OnboardingPromptType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: OnboardingPromptType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): OnboardingPromptType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [OnboardingPromptType]s.
         */
        public val entries: List<OnboardingPromptType> by lazy(mode = PUBLICATION) {
            listOf(
                MultipleChoice,
                Dropdown,
            )
        }

        /**
         * Returns an instance of [OnboardingPromptType] with [OnboardingPromptType.value] equal to
         * the specified [value].
         */
        public fun from(`value`: Int): OnboardingPromptType = when (value) {
            0 -> MultipleChoice
            1 -> Dropdown
            else -> Unknown(value)
        }
    }
}
