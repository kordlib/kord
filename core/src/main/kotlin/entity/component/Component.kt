package dev.kord.core.entity.component

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ComponentType
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.entity.Message

/**
 * An interactive element inside a [Message].
 */
@KordPreview
sealed interface Component {

    /**
     * The type of component.
     * @see ButtonComponent
     * @see ActionRowComponent
     * @see UnknownComponent
     */
    val type: ComponentType get() = data.type

    val data: ComponentData
}

/**
 * Creates a [Component] from the [data].
 * @see ActionRowComponent
 * @see ButtonComponent
 * @see UnknownComponent
 */
@KordPreview
fun Component(data: ComponentData): Component = when (data.type) {
    ComponentType.ActionRow -> ActionRowComponent(data)
    ComponentType.Button -> ButtonComponent(data)
    is ComponentType.Unknown -> UnknownComponent(data)
}
