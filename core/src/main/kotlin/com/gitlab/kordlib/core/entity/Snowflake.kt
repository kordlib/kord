package com.gitlab.kordlib.core.entity

inline class Snowflake(val value: String) {
    constructor(value: Long) : this(value.toString())

    val longValue get() = value.toLong()
}
