package com.gitlab.kordlib.core.entity

inline class Snowflake(val value: Long) {
    override fun toString() = value.toString()
}
