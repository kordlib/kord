package dev.kord.rest.route

import dev.kord.common.entity.Snowflake

public sealed interface Position {
    public val key: String
    public val value: Snowflake

    public sealed interface BeforeOrAfter : Position

    public class Before(id: Snowflake) : BeforeOrAfter {
        override val key: String get() = "before"
        override val value: Snowflake = id
    }

    public class After(id: Snowflake) : BeforeOrAfter {
        override val key: String get() = "after"
        override val value: Snowflake = id
    }

    public class Around(id: Snowflake) : Position {
        override val key: String get() = "around"
        override val value: Snowflake = id
    }
}
