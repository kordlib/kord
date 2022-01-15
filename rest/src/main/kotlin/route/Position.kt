package dev.kord.rest.route

import dev.kord.common.entity.Snowflake

sealed interface Position {
    val key: String
    val value: Snowflake

    sealed interface BeforeOrAfter : Position

    class Before(id: Snowflake) : BeforeOrAfter {
        override val key get() = "before"
        override val value = id
    }

    class After(id: Snowflake) : BeforeOrAfter {
        override val key get() = "after"
        override val value = id
    }

    class Around(id: Snowflake) : Position {
        override val key get() = "around"
        override val value = id
    }
}
