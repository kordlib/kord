package dev.kord.core.cache

import dev.kord.common.entity.Snowflake

public data class UnionSnowflake(val snowflakes: Map<String, Snowflake>) {

    public fun intersects(key: String, with: UnionSnowflake): Boolean {
        val singleSnowflake = snowflakes[key] ?: return false
        return singleSnowflake == with.snowflakes[key]
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other is Snowflake) return false
        if(other !is UnionSnowflake) return false
        return snowflakes == other.snowflakes
    }

    override fun hashCode(): Int {
        return snowflakes.hashCode()
    }
}

public fun UnionSnowflake(builderAction: MutableMap<String, Snowflake>.() -> Unit): UnionSnowflake {
    val map = mutableMapOf<String, Snowflake>().apply(builderAction)
    return UnionSnowflake(map)
}