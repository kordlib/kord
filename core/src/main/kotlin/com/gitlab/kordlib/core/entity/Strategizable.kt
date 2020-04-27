package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.EntitySupplyStrategy

interface Strategizable {

    val strategy: EntitySupplyStrategy
}