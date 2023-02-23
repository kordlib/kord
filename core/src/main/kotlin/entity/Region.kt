package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.RegionData
import java.util.*

/**
 * Represents a Voice Region for a guild.
 *
 * @param data The [RegionData] for the guild.
 */
public class Region(public val data: RegionData, override val kord: Kord) : KordObject {
    /** The unique ID for the region. */
    public val id: String
        get() = data.id

    /** Whether this is a custom voice region (used for events/etc) */
    public val isCustom: Boolean get() = data.custom

    /** Whether this is a deprecated voice region (avoid switching to these) */
    public val isDeprecated: Boolean get() = data.deprecated

    /** The name of the region. */
    public val name: String get() = data.name

    /** True for a single server that is closest to the current user's client. */
    public val isOptimal: Boolean get() = data.optimal

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is Region -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Region(data=$data, kord=$kord)"
    }

}
