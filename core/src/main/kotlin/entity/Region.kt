package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.RegionData
import java.util.*

public class Region(public val data: RegionData, override val kord: Kord) : KordObject {
    public val id: String
        get() = data.id

    public val isCustom: Boolean get() = data.custom

    public val isDeprecated: Boolean get() = data.deprecated

    public val name: String get() = data.name

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
