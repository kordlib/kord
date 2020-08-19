package com.gitlab.kordlib.common.entity

import java.time.Instant
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.toKotlinDuration

/**
 * A unique identifier for entities [used by discord](https://discord.com/developers/docs/reference#snowflakes).
 */
inline class Snowflake(val longValue: Long) : Comparable<Snowflake> {
    constructor(value: String) : this(value.toLong())

    val value get() = longValue.toString()

    val timeStamp: Instant get() = Instant.ofEpochMilli(discordEpoch + (longValue shr 22))

    val timeMark: TimeMark get() = SnowflakeMark(longValue shr 22)

    override fun compareTo(other: Snowflake): Int = longValue.shr(22).compareTo(other.longValue.shr(22))

    companion object {
        const val discordEpoch = 1420070400000L
    }
}

private class SnowflakeMark(val epochMilliseconds: Long) : TimeMark() {

    override fun elapsedNow(): Duration =
            java.time.Duration.between(Instant.ofEpochMilli(epochMilliseconds), Instant.now()).toKotlinDuration()

}