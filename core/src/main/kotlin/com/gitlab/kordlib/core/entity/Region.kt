package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.RegionData

class Region(val data: RegionData, override val kord: Kord) : Entity {
    override val id: Snowflake
        get() = Snowflake(data.id)

    val isCustom: Boolean get() = data.custom

    val isDeprecated: Boolean get() = data.deprecated

    val name: String get() = data.name

    val isOptimal: Boolean get() = data.optimal

    val isVip: Boolean get() = data.vip
}