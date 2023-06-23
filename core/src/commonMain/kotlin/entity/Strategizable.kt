package dev.kord.core.entity

import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A class that will defer the requesting of [Entities][KordEntity] to a [supplier].
 * Copies of this class with a different [supplier] can be made through [withStrategy].
 *
 * Unless stated otherwise, all members that fetch [Entities][KordEntity] will delegate to the [supplier].
 */
public interface Strategizable {

    /**
     * The supplier used to request entities.
     */
    public val supplier: EntitySupplier


    /**
     * Returns a copy of this class with a new [supplier] provided by the [strategy].
     */
    public fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable

}
