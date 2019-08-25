package com.gitlab.kordlib.core.entity

/**
 * A unique identifier for entities [used by discord](https://discordapp.com/developers/docs/reference#snowflakes).
 */
inline class Snowflake(val value: String) : Comparable<Snowflake> {
    constructor(value: Long) : this(value.toString())

    val longValue get() = value.toLong()

    override fun compareTo(other: Snowflake): Int = longValue.shr(22).compareTo(other.longValue.shr(22))
}
