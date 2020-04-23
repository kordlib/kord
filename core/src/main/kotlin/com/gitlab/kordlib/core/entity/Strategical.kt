package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.EntitySupplyStrategy

interface Strategilizable {

    val strategy: EntitySupplyStrategy
}