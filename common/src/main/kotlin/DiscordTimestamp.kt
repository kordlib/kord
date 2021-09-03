package dev.kord.common

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

/**
 * The [timestamp format](https://discord.com/developers/docs/reference#message-formatting-formats) of the message formats
 */
@Serializable
data class DiscordTimestamp(

    /**
     * The Unix timestamp to be displayed
     */
    val time: Long,

    /**
     * The style of the timestamp
     */
    val style: DiscordTimestampStyle = DiscordTimestampStyle.ShortDateTime
) : Comparable<DiscordTimestamp> {

    /**
     * Assemble the timestamp to the format supported by discord
     */
    override fun toString(): String = "<t:$time:${style.style}>"

    override fun equals(other: Any?): Boolean = other is DiscordTimestamp && other.time == time
    operator fun plus(value: Long): DiscordTimestamp = DiscordTimestamp(time + value, style)
    operator fun minus(value: Long): DiscordTimestamp = DiscordTimestamp(time - value, style)

    override fun compareTo(other: DiscordTimestamp): Int = when {
        time > other.time -> 1
        time < other.time -> -1
        else -> 0
    }
}

fun LocalDateTime.toDiscordTimestamp(style: DiscordTimestampStyle? = null) =
    DiscordTimestamp(toInstant(TimeZone.UTC).toEpochMilliseconds(), style ?: DiscordTimestampStyle.ShortDateTime)

fun Instant.toDiscordTimestamp(style: DiscordTimestampStyle? = null) =
    DiscordTimestamp(toEpochMilliseconds(), style ?: DiscordTimestampStyle.ShortDateTime)

/**
 * The class representing the [style of a timestamp](https://discord.com/developers/docs/reference#message-formatting-timestamp-styles)
 */
enum class DiscordTimestampStyle(val style: String) {

    /**
     * For example 16:20
     */
    ShortTime("t"),

    /**
     * For example 16:20:30
     */
    LongTime("T"),

    /**
     * For example 20/04/2021
     */
    ShortDate("d"),

    /**
     * For example 20 April 2021
     */
    LongDate("D"),

    /**
     * For example 20 April 2021 16:20
     */
    ShortDateTime("f"),

    /**
     * For example Tuesday, 20 April 2021 16:20
     */
    LongDateTime("F"),

    /**
     * For example 2 months ago
     */
    RelativeTime("R"),
}
