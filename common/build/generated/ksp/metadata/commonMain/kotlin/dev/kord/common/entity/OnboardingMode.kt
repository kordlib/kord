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
 * Defines the criteria used to satisfy Onboarding constraints that are required for enabling.
 *
 * See [OnboardingMode]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/guild#guild-onboarding-object-onboarding-mode).
 */
@Serializable(with = OnboardingMode.Serializer::class)
public sealed class OnboardingMode(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is OnboardingMode && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "OnboardingMode.Unknown(value=$value)"
            else "OnboardingMode.${this::class.simpleName}"

    /**
     * An unknown [OnboardingMode].
     *
     * This is used as a fallback for [OnboardingMode]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : OnboardingMode(value)

    /**
     * Counts only Default Channels towards constraints.
     */
    public object Default : OnboardingMode(0)

    /**
     * Counts Default Channels and Questions towards constraints.
     */
    public object Advanced : OnboardingMode(1)

    internal object Serializer : KSerializer<OnboardingMode> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.OnboardingMode",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: OnboardingMode) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): OnboardingMode = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [OnboardingMode]s.
         */
        public val entries: List<OnboardingMode> by lazy(mode = PUBLICATION) {
            listOf(
                Default,
                Advanced,
            )
        }


        /**
         * Returns an instance of [OnboardingMode] with [OnboardingMode.value] equal to the
         * specified [value].
         */
        public fun from(`value`: Int): OnboardingMode = when (value) {
            0 -> Default
            1 -> Advanced
            else -> Unknown(value)
        }
    }
}
