package dev.kord.core.entity.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.TextInputComponent
import dev.kord.core.cache.data.ActionRowComponentData
import dev.kord.core.cache.data.ButtonComponentData
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.cache.data.ModalResponseTextInputComponentData
import dev.kord.core.cache.data.SelectMenuComponentData
import dev.kord.core.cache.data.TextInputComponentData
import dev.kord.core.cache.data.UnknownComponentData
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

public fun Component(data: ComponentData): Component = when (data) {
    is ActionRowComponentData -> ActionRowComponent(data)
    is ButtonComponentData -> ButtonComponent(data)
    is SelectMenuComponentData -> SelectMenuComponent(data)
    is TextInputComponentData -> TextInputComponent(data)
    is ModalResponseTextInputComponentData -> ModalResponseTextInputComponent(data)
    is UnknownComponentData -> UnknownComponent(data)
}
