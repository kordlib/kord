package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.KordObject

/**
 * An object that is identified by its [id].
 */
interface Entity : KordObject, Comparable<Entity> {
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
