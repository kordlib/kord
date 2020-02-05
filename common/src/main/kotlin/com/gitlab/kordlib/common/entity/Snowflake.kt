package com.gitlab.kordlib.common.entity

import java.time.Instant
import kotlin.time.ClockMark
import kotlin.time.Duration
import kotlin.time.toKotlinDuration


/**
 * A unique identifier for entities [used by discord](https://discordapp.com/developers/docs/reference#snowflakes).
 */
inline class Snowflake(val longValue: Long) : Comparable<Snowflake> {
    constructor(value: String) : this(value.toLong())

    val value get() = longValue.toString()

    val timeStamp: Instant get() = Instant.ofEpochMilli(discordEpoch + (longValue shr 22))

    val timeMark: ClockMark get() = SnowflakeMark(longValue shr 22)

    override fun compareTo(other: Snowflake): Int = longValue.shr(22).compareTo(other.longValue.shr(22))

    companion object {
        val discordEpoch = 1420070400000L
    }
}

private class SnowflakeMark(val epochMilliseconds: Long) : ClockMark() {

    override fun elapsedNow(): Duration =
            java.time.Duration.between(Instant.ofEpochMilli(epochMilliseconds), Instant.now()).toKotlinDuration()

}