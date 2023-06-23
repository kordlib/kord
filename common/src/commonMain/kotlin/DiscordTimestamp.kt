package dev.kord.common

import kotlinx.datetime.Instant

public fun Instant.toMessageFormat(style: DiscordTimestampStyle? = null): String =
    if (style == null) "<t:$epochSeconds>" else "<t:$epochSeconds:${style.style}>"

/**
 * The class representing the [style of a timestamp](https://discord.com/developers/docs/reference#message-formatting-timestamp-styles)
 */
public enum class DiscordTimestampStyle(public val style: String) {

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
