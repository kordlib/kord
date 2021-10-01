package dev.kord.core

/**
 * An instance than contains a reference to [Kord].
 */
public interface KordObject {

    /**
     * The kord instance that created this object.
     */
    public val kord: Kord
}
