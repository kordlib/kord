package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.KordObject

/**
 * An object that is identified by its [id].
 */
interface Entity : KordObject {
    /**
     * The unique identifier of this entity.
     */
    val id: Snowflake
}
