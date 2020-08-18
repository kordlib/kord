package com.gitlab.kordlib.core

/**
 * An instance than contains a reference to [Kord].
 */
interface KordObject {

    /**
     * The kord instance that created this object.
     */
    val kord: Kord
}