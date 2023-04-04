package dev.kord.core.cache.api

import dev.kord.common.entity.Snowflake

public data class MutliSnowflakeIndex(public val snowflakes: Set<Snowflake>): Index {
    public fun getValue(): String {
        return snowflakes.toString()
    }

    override fun compareTo(other: Index): Int {
        return this.hashCode() - other.hashCode()
    }

    override fun hashCode(): Int {
        return snowflakes.hashCode()
    }

}