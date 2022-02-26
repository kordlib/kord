package dev.kord.core.entity.component

import dev.kord.common.entity.ComponentType
import dev.kord.core.cache.data.ChatComponentData
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.cache.data.TextInputComponentData
import dev.kord.core.entity.Message

/**
 * An interactive element inside a [Message].
 */

public sealed interface Component {

    /**
     * The type of component.
     * @see ButtonComponent
     * @see ActionRowComponent
     * @see SelectMenuComponent
     * @see UnknownComponent
     */
    public val type: ComponentType get() = data.type

    public val data: ComponentData
}

/**
 * Creates a [Component] from the [data].
 * @see ActionRowComponent
 * @see ButtonComponent
 * @see SelectMenuComponent
 * @see UnknownComponent
 */
public fun Component(data: ComponentData): Component = when (data.type) {
    ComponentType.ActionRow -> ActionRowComponent(data)
    ComponentType.Button -> ButtonComponent(data as ChatComponentData)
    ComponentType.SelectMenu -> SelectMenuComponent(data)
    ComponentType.TextInput -> TextInputComponent(data as TextInputComponentData)
    is ComponentType.Unknown -> UnknownComponent(data)
}
