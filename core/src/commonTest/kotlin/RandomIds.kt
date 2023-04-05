package dev.kord.core

import dev.kord.common.entity.Snowflake
import kotlin.random.Random
import kotlin.random.nextULong

val ids = generateSequence {
    Random.nextULong(Snowflake.validValues) // limit to valid range to guarantee distinct generated Snowflakes
}.distinct().iterator()

fun randomId() = Snowflake(ids.next())
