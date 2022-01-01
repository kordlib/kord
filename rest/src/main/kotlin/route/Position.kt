package dev.kord.rest.route

import dev.kord.common.entity.Snowflake

public sealed class Position(public val key: String, public val value: Snowflake) {
    public class Before(id: Snowflake) : Position("before", id)
    public class After(id: Snowflake) : Position("after", id)
    public class Around(id: Snowflake) : Position("around", id)
}
