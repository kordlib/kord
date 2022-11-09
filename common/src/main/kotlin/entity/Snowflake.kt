package dev.kord.common.entity

import dev.kord.common.entity.Snowflake.Companion.validValues
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.DeprecationLevel.HIDDEN
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
@JvmInline
public value class Snowflake
/**
 * Creates a Snowflake from a given ULong [value].
 *
 * Values are [coerced in][coerceIn] [validValues].
 */
constructor(
    /**
     * The raw value of this Snowflake as specified
     * [here](https://discord.com/developers/docs/reference#snowflakes).
     */
    public val value: ULong
) : Comparable<Snowflake> {
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
            .coerceAtLeast(DISCORD_EPOCH_LONG) // time before is unknown to Snowflakes
            .minus(DISCORD_EPOCH_LONG)
            .toULong()
            .coerceAtMost(maxMillisecondsSinceDiscordEpoch) // time after is unknown to Snowflakes
            .shl(TIMESTAMP_SHIFT)
    )

    private inline val millisecondsSinceDiscordEpoch get() = value shr TIMESTAMP_SHIFT

    /**
     * A [String] representation of this Snowflake's [value].
     *
     * @suppress
     */
    @Deprecated("Use toString() instead", ReplaceWith("toString()"), level = HIDDEN)
    public val asString: String
        get() = value.toString()

    /**
     * The point in time this Snowflake represents.
     */
    public val timestamp: Instant
        get() = Instant.fromEpochMilliseconds(DISCORD_EPOCH_LONG + millisecondsSinceDiscordEpoch.toLong())

    /**
     * A [TimeMark] for the point in time this Snowflake represents.
     */
    public val timeMark: TimeMark
        get() = SnowflakeTimeMark(timestamp)

    /**
     * Internal ID of the worker that generated this Snowflake ID.
     *
     * Only the 5 least significant bits are used. This value is therefore always in the range `0..31`.
     */
    public val workerId: UByte
        get() = value.and(WORKER_MASK).shr(WORKER_SHIFT).toUByte()

    /**
     * Internal ID of the process that generated this Snowflake ID.
     *
     * Only the 5 least significant bits are used. This value is therefore always in the range `0..31`.
     */
    public val processId: UByte
        get() = value.and(PROCESS_MASK).shr(PROCESS_SHIFT).toUByte()

    /**
     * Increment. For every Snowflake ID that is generated on a [process][processId], this number is incremented.
     *
     * Only the 12 least significant bits are used. This value is therefore always in the range `0..4095`.
     */
    public val increment: UShort
        get() = value.and(INCREMENT_MASK).toUShort()


    /**
     * Returns [timestamp] for use in destructuring declarations.
     *
     * ```kotlin
     * val (timestamp, workerId, processId, increment) = snowflake
     * ```
     */
    public operator fun component1(): Instant = timestamp

    /**
     * Returns [workerId] for use in destructuring declarations.
     *
     * ```kotlin
     * val (timestamp, workerId, processId, increment) = snowflake
     * ```
     */
    public operator fun component2(): UByte = workerId

    /**
     * Returns [processId] for use in destructuring declarations.
     *
     * ```kotlin
     * val (timestamp, workerId, processId, increment) = snowflake
     * ```
     */
    public operator fun component3(): UByte = processId

    /**
     * Returns [increment] for use in destructuring declarations.
     *
     * ```kotlin
     * val (timestamp, workerId, processId, increment) = snowflake
     * ```
     */
    public operator fun component4(): UShort = increment


    override fun compareTo(other: Snowflake): Int =
        millisecondsSinceDiscordEpoch.compareTo(other.millisecondsSinceDiscordEpoch)

    /**
     * A [String] representation of this Snowflake's [value].
     */
    override fun toString(): String = value.toString()

    public companion object {
        // see https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right

        private const val DISCORD_EPOCH_LONG = 1420070400000L

        private const val TIMESTAMP_SHIFT = 22

        private const val WORKER_MASK = 0x3E0000uL
        private const val WORKER_SHIFT = 17

        private const val PROCESS_MASK = 0x1F000uL
        private const val PROCESS_SHIFT = 12

        private const val INCREMENT_MASK = 0xFFFuL


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
        public val discordEpoch: Instant = Instant.fromEpochMilliseconds(DISCORD_EPOCH_LONG)

        /**
         * The last point in time a Snowflake can represent.
         */
        public val endOfTime: Instant = max.timestamp

        private val maxMillisecondsSinceDiscordEpoch = max.millisecondsSinceDiscordEpoch
    }

    internal object Serializer : KSerializer<Snowflake> {
        override val descriptor: SerialDescriptor = ULong.serializer().descriptor

        override fun deserialize(decoder: Decoder): Snowflake =
            Snowflake(decoder.decodeInline(descriptor).decodeLong().toULong())

        override fun serialize(encoder: Encoder, value: Snowflake) {
            encoder.encodeInline(descriptor).encodeLong(value.value.toLong())
        }
    }
}

private class SnowflakeTimeMark(private val timestamp: Instant) : TimeMark {

    override fun elapsedNow(): Duration = Clock.System.now() - timestamp
}

/**
 * Creates a [Snowflake] from a given Long [value].
 *
 * Values are [coerced in][coerceIn] [validValues].
 */
public fun Snowflake(value: Long): Snowflake = Snowflake(value.coerceAtLeast(0).toULong())
