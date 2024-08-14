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
 * See [ActivityType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-types).
 */
@Serializable(with = ActivityType.Serializer::class)
public sealed class ActivityType(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is ActivityType && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()

    final override fun toString(): String = if (this is Unknown) "ActivityType.Unknown(code=$code)"
            else "ActivityType.${this::class.simpleName}"

    /**
     * An unknown [ActivityType].
     *
     * This is used as a fallback for [ActivityType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        code: Int,
    ) : ActivityType(code)

    public object Game : ActivityType(0)

    public object Streaming : ActivityType(1)

    public object Listening : ActivityType(2)

    public object Watching : ActivityType(3)

    public object Custom : ActivityType(4)

    public object Competing : ActivityType(5)

    internal object Serializer : KSerializer<ActivityType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.ActivityType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: ActivityType) {
            encoder.encodeInt(value.code)
        }

        override fun deserialize(decoder: Decoder): ActivityType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [ActivityType]s.
         */
        public val entries: List<ActivityType> by lazy(mode = PUBLICATION) {
            listOf(
                Game,
                Streaming,
                Listening,
                Watching,
                Custom,
                Competing,
            )
        }

        /**
         * Returns an instance of [ActivityType] with [ActivityType.code] equal to the specified
         * [code].
         */
        public fun from(code: Int): ActivityType = when (code) {
            0 -> Game
            1 -> Streaming
            2 -> Listening
            3 -> Watching
            4 -> Custom
            5 -> Competing
            else -> Unknown(code)
        }
    }
}
