package dev.kord.common.entity

import dev.kord.common.entity.Snowflake.Companion.validValues
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
 * Snowflakes are IDs with a [timestamp], which makes them [comparable][Comparable] based on their timestamp.
 *
 * Note: this class has a natural ordering that is inconsistent with [equals],
 * since [compareTo] only compares the first 42 bits of the ULong [value] (comparing the timestamp),
 * whereas [equals] uses all bits of the ULong [value].
 * [compareTo] can return `0` even if [equals] returns `false`,
 * but [equals] only returns `true` if [compareTo] returns `0`.
 */
@Serializable(with = Snowflake.Serializer::class)
public class Snowflake : Comparable<Snowflake> {

    /**
     * The raw value of this Snowflake as specified
     * [here](https://discord.com/developers/docs/reference#snowflakes).
     */
    public val value: ULong

    /**
     * Creates a Snowflake from a given ULong [value].
     *
     * Values are [coerced in][coerceIn] [validValues].
     */
    public constructor(value: ULong) {
        this.value = value.coerceIn(validValues)
    }

    /**
     * Creates a Snowflake from a given String [value], parsing it as a [ULong] value.
     *
     * Values are [coerced in][coerceIn] [validValues].
     */
    public constructor(value: String) : this(value.toULong())

    /**
     * Creates a Snowflake from a given [timestamp].
     *
     * If the given timestamp is too far in the past / future, this constructor will create an instance with a
     * [timestamp][Snowflake.timestamp] equal to the timestamp of [Snowflake.min] / [Snowflake.max].
     */
    public constructor(timestamp: Instant) : this(
        timestamp.toEpochMilliseconds()
            .coerceAtLeast(discordEpochLong) // time before is unknown to Snowflakes
            .minus(discordEpochLong)
            .toULong()
            .coerceAtMost(maxMillisecondsSinceDiscordEpoch) // time after is unknown to Snowflakes
            .shl(nonTimestampBitCount)
    )

    private val millisecondsSinceDiscordEpoch get() = value shr nonTimestampBitCount

    /**
     * A [String] representation of this Snowflake's [value].
     */
    @Deprecated("Use toString() instead", ReplaceWith("toString()"))
    public val asString: String
        get() = value.toString()

    /**
     * The point in time this Snowflake represents.
     */
    @Deprecated("timeStamp was renamed to timestamp.", ReplaceWith("timestamp"), DeprecationLevel.ERROR)
    public val timeStamp: Instant
        get() = timestamp

    /**
     * The point in time this Snowflake represents.
     */
    public val timestamp: Instant
        get() = Instant.fromEpochMilliseconds(discordEpochLong + millisecondsSinceDiscordEpoch.toLong())

    /**
     * A [TimeMark] for the point in time this Snowflake represents.
     */
    public val timeMark: TimeMark
        get() = SnowflakeTimeMark(timestamp)

    override fun compareTo(other: Snowflake): Int =
        millisecondsSinceDiscordEpoch.compareTo(other.millisecondsSinceDiscordEpoch)

    /**
     * A [String] representation of this Snowflake's [value].
     */
    override fun toString(): String = value.toString()

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = other is Snowflake && this.value == other.value

    public companion object {
        private const val nonTimestampBitCount = 22
        private const val discordEpochLong = 1420070400000L

        /**
         * A range that contains all valid raw Snowflake [value]s.
         *
         * Note that this range might change in the future.
         */
        public val validValues: ULongRange = ULong.MIN_VALUE..Long.MAX_VALUE.toULong() // 0..9223372036854775807

        /**
         * The minimum value a Snowflake can hold.
         * Useful when requesting paginated entities.
         */
        public val min: Snowflake = Snowflake(validValues.first)

        /**
         * The maximum value a Snowflake can hold.
         * Useful when requesting paginated entities.
         */
        public val max: Snowflake = Snowflake(validValues.last)

        /**
         * The point in time that marks the Discord Epoch (the first second of 2015).
         */
        @Deprecated(
            "Snowflake.discordEpochStart was renamed to Snowflake.discordEpoch.",
            ReplaceWith("Snowflake.discordEpoch"),
            DeprecationLevel.ERROR,
        )
        public val discordEpochStart: Instant
            get() = discordEpoch

        /**
         * The point in time that marks the Discord Epoch (the first second of 2015).
         */
        public val discordEpoch: Instant = Instant.fromEpochMilliseconds(discordEpochLong)

        /**
         * The last point in time a Snowflake can represent.
         */
        public val endOfTime: Instant = max.timestamp

        private val maxMillisecondsSinceDiscordEpoch = max.millisecondsSinceDiscordEpoch
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

private class SnowflakeTimeMark(private val timestamp: Instant) : TimeMark() {

    override fun elapsedNow(): Duration = Clock.System.now() - timestamp
}

/**
 * Creates a [Snowflake] from a given Long [value].
 *
 * Values are [coerced in][coerceIn] [validValues].
 */
public fun Snowflake(value: Long): Snowflake = Snowflake(value.coerceAtLeast(0).toULong())
