package dev.kord.rest.route

import dev.kord.common.entity.Snowflake

sealed class Position(val key: String, val value: Snowflake) {
    class Before(id: Snowflake) : Position("before", id)
    class After(id: Snowflake) : Position("after", id)
    class Around(id: Snowflake) : Position("around", id)
}