package dev.kord.core.cache.api

import dev.kord.common.entity.Snowflake

/**
 * An implementation of [Index] that represents a unique identifier for a set of [Snowflake] objects.
 */
public data class UnionSnowflakeIndex(public val snowflakes: Set<Snowflake>): Index {

    /**
     * Returns the string representation of this [UnionSnowflakeIndex] object.
     */
    public fun getValue(): String {
        return snowflakes.toString()
    }

    /**
     * Compares this [UnionSnowflakeIndex] object with another [Index] object.
     * Returns a negative integer, zero, or a positive integer if this object is less than, equal to, or greater than the specified object.
     */
    override fun compareTo(other: Index): Int {
        return this.hashCode() - other.hashCode()
    }

    /**
     * Returns the hash code for this [UnionSnowflakeIndex] object.
     */
    override fun hashCode(): Int {
        return snowflakes.hashCode()
    }

}
