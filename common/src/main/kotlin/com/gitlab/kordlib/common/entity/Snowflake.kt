package dev.kord.common.entity

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.toKotlinDuration

/**
 * A unique identifier for entities [used by discord](https://discord.com/developers/docs/reference#snowflakes).
 *
 * @constructor Creates a Snowflake from a given Long [value].
 */
@Serializable(with = Snowflake.Serializer::class)
class Snowflake(val value: Long) : Comparable<Snowflake> {

    /**
     * Creates a Snowflake from a given String [value], parsing it a [Long] value.
     */
    constructor(value: String) : this(value.toLong())

    /**
     * Creates a Snowflake from a given [instant].
     */
    constructor(instant: Instant): this((instant.toEpochMilli() shl 22) - discordEpochLong)

    val asString get() = value.toString()

    val timeStamp: Instant get() = Instant.ofEpochMilli(discordEpochLong + (value shr 22))

    val timeMark: TimeMark get() = SnowflakeMark(value shr 22)

    override fun compareTo(other: Snowflake): Int = value.shr(22).compareTo(other.value.shr(22))

    override fun toString(): String = "Snowflake(value=$value)"

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean {
        return (other as? Snowflake ?: return false).value == value
    }

    companion object {
        private const val discordEpochLong = 1420070400000L
        val discordEpochStart: Instant = Instant.ofEpochMilli(discordEpochLong)

        /**
         * The maximum value a Snowflake can hold.
         * Useful when requesting paginated entities.
         */
        val max: Snowflake = Snowflake(Long.MAX_VALUE)

        /**
         * The minimum value a Snowflake can hold.
         * Useful when requesting paginated entities.
         */
        val min: Snowflake = Snowflake(0)

    }

    internal class Serializer : KSerializer<Snowflake> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.Snowflake", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): Snowflake = Snowflake(decoder.decodeLong())

        override fun serialize(encoder: Encoder, value: Snowflake) {
            encoder.encodeString(value.value.toString())
        }
    }
}

private class SnowflakeMark(val epochMilliseconds: Long) : TimeMark() {

    override fun elapsedNow(): Duration =
            java.time.Duration.between(Instant.ofEpochMilli(epochMilliseconds), Instant.now()).toKotlinDuration()

}