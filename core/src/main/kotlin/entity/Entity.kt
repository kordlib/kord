package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.KordObject

interface Entity : Comparable<Entity> {
    /**
     * The unique identifier of this entity.
     */
    val id: Snowflake

    /**
     * Compares entities on [id].
     */
    override operator fun compareTo(other: Entity): Int = comparator.compare(this, other)

    companion object {
        val comparator = compareBy<Entity> { it.id }
    }
}

/**
 * An object that is identified by its [id].
 * This object holds a [KordObject]
 */
interface KordEntity : KordObject, Entity
