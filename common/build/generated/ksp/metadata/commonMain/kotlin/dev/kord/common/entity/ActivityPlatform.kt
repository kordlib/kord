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
 * See [ActivityPlatform]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/events/gateway-events#activity-object).
 */
@Serializable(with = ActivityPlatform.Serializer::class)
public sealed class ActivityPlatform(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ActivityPlatform && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "ActivityPlatform.Unknown(value=$value)"
            else "ActivityPlatform.${this::class.simpleName}"

    /**
     * An unknown [ActivityPlatform].
     *
     * This is used as a fallback for [ActivityPlatform]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : ActivityPlatform(value)

    public object Desktop : ActivityPlatform("desktop")

    public object Xbox : ActivityPlatform("xbox")

    public object Samsung : ActivityPlatform("samsung")

    public object Ios : ActivityPlatform("ios")

    public object Android : ActivityPlatform("android")

    public object Embedded : ActivityPlatform("embedded")

    public object PS4 : ActivityPlatform("ps4")

    public object PS5 : ActivityPlatform("ps5")

    internal object Serializer : KSerializer<ActivityPlatform> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ActivityPlatform",
                PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: ActivityPlatform) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): ActivityPlatform = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [ActivityPlatform]s.
         */
        public val entries: List<ActivityPlatform> by lazy(mode = PUBLICATION) {
            listOf(
                Desktop,
                Xbox,
                Samsung,
                Ios,
                Android,
                Embedded,
                PS4,
                PS5,
            )
        }

        /**
         * Returns an instance of [ActivityPlatform] with [ActivityPlatform.value] equal to the
         * specified [value].
         */
        public fun from(`value`: String): ActivityPlatform = when (value) {
            "desktop" -> Desktop
            "xbox" -> Xbox
            "samsung" -> Samsung
            "ios" -> Ios
            "android" -> Android
            "embedded" -> Embedded
            "ps4" -> PS4
            "ps5" -> PS5
            else -> Unknown(value)
        }
    }
}
