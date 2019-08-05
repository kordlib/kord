package com.gitlab.kordlib.core.entity

inline class Snowflake(val value: String) : Comparable<Snowflake> {
    constructor(value: Long) : this(value.toString())

    val longValue get() = value.toLong()

    override fun compareTo(other: Snowflake): Int = longValue.compareTo(other.longValue )
}
