package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.cache.data.RegionData
import java.util.*

class Region(val data: RegionData, override val kord: Kord) : KordObject {
    val id: String
        get() = data.id

    val isCustom: Boolean get() = data.custom

    val isDeprecated: Boolean get() = data.deprecated

    val name: String get() = data.name

    val isOptimal: Boolean get() = data.optimal

    val isVip: Boolean get() = data.vip

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is Region -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Region(data=$data, kord=$kord)"
    }

}
