package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

interface Strategizable {

    val supplier: EntitySupplier

    fun withStrategy(strategy: EntitySupplyStrategy<*>) : Strategizable

}