package dev.kord.common.entity

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.TimeMark

/**
 * A unique identifier for entities [used by discord](https://discord.com/developers/docs/reference#snowflakes).
 *
 * Note: this class has a natural ordering that is inconsistent with [equals],
 * since [compareTo] only compares the first 42 bits of the ULong [value] (comparing the timestamp),
 * whereas [equals] uses all bits of the ULong [value].
 * [compareTo] can return `0` even if [equals] returns `false`,
 * but [equals] only returns `true` if [compareTo] returns `0`.
 *
 * @constructor Creates a Snowflake from a given ULong [value].
 */
@Serializable(with = Snowflake.Serializer::class)
class Snowflake(val value: ULong) : Comparable<Snowflake> {

    /**
     * Creates a Snowflake from a given String [value], parsing it as a [ULong] value.
     */
    constructor(value: String) : this(value.toULong())

    /**
     * Creates a Snowflake from a given [instant].
     *
     * If the given [instant] is too far in the past / future, this constructor will create
     * an instance with a [timeStamp] equal to [Snowflake.min] / [Snowflake.max].
     */
    constructor(instant: Instant) : this(
        instant.toEpochMilliseconds()
            .coerceAtLeast(discordEpochLong) // time before is unknown to Snowflakes
            .minus(discordEpochLong)
            .coerceAtMost(maxMillisecondsSinceDiscordEpoch) // time after is unknown to Snowflakes
            .toULong()
            .shl(22)
    )

    /**
     * A [String] representation of this Snowflake's [value].
     */
    val asString get() = value.toString()

    /**
     * The point in time this Snowflake represents.
     */
    val timeStamp: Instant get() = Instant.fromEpochMilliseconds(discordEpochLong + (value shr 22).toLong())

    /**
     * A [TimeMark] for the point in time this Snowflake represents.
     */
    val timeMark: TimeMark get() = SnowflakeTimeMark(timeStamp)

    override fun compareTo(other: Snowflake): Int = value.shr(22).compareTo(other.value.shr(22))

    override fun toString(): String = "Snowflake(value=$value)"

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean {
        return (other as? Snowflake ?: return false).value == value
    }

    companion object {
        private const val discordEpochLong = 1420070400000L                                 // 42 one bits
        private const val maxMillisecondsSinceDiscordEpoch = 0b111111111111111111111111111111111111111111L

        /**
         * The point in time that marks the Discord Epoch (the first second of 2015).
         */
        val discordEpochStart: Instant = Instant.fromEpochMilliseconds(discordEpochLong)

        /**
         * The last point in time a Snowflake can represent.
         */
        val endOfTime: Instant = Instant.fromEpochMilliseconds(discordEpochLong + maxMillisecondsSinceDiscordEpoch)

        /**
         * The maximum value a Snowflake can hold.
         * Useful when requesting paginated entities.
         */
        val max: Snowflake = Snowflake(ULong.MAX_VALUE)

        /**
         * The minimum value a Snowflake can hold.
         * Useful when requesting paginated entities.
         */
        val min: Snowflake = Snowflake(ULong.MIN_VALUE)
    }

    @OptIn(ExperimentalSerializationApi::class)
    internal object Serializer : KSerializer<Snowflake> {
        override val descriptor: SerialDescriptor =
            @OptIn(ExperimentalUnsignedTypes::class) ULong.serializer().descriptor

        override fun deserialize(decoder: Decoder): Snowflake =
            Snowflake(decoder.decodeInline(descriptor).decodeLong().toULong())

        override fun serialize(encoder: Encoder, value: Snowflake) {
            encoder.encodeInline(descriptor).encodeLong(value.value.toLong())
        }
    }
}

private class SnowflakeTimeMark(private val timeStamp: Instant) : TimeMark() {

    override fun elapsedNow(): Duration = Clock.System.now() - timeStamp
}

/**
 * Creates a [Snowflake] from a given Long [value].
 *
 * Note: a negative [value] will be interpreted as an unsigned integer with the same binary representation, e.g.
 * passing `-1L` for [value] will return a [Snowflake] with a [value][Snowflake.value] of [ULong.MAX_VALUE].
 */
fun Snowflake(value: Long): Snowflake = Snowflake(value.toULong())
