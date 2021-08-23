package dev.kord.core.entity.component

import dev.kord.common.annotation.KordPreview
import dev.kord.core.cache.data.ComponentData

/**
 * A component type unknown to Kord.
 */

class UnknownComponent(override val data: ComponentData) : Component {

    override fun toString(): String = "UnknownComponent(data=$data)"

}
