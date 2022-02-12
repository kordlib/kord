package dev.kord.core.entity.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.ComponentData

/**
 * A non-interactive container component for other types of component.
 */

public class ActionRowComponent(override val data: ComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.ActionRow

    /**
     * All components nested inside this component.
     */
    public val components: List<Component>
        get() = data.components.orEmpty().map { Component(it) }

    /**
     * The buttons that are nested inside this component.
     * @see components
     */
    public val buttons: List<ButtonComponent>
        get() = components.filterIsInstance<ButtonComponent>()

    /**
     * The buttons that are nested inside this component.
     * @see components
     */
    public val selectMenus: List<SelectMenuComponent>
        get() = components.filterIsInstance<SelectMenuComponent>()

    /**
     * The [TextInputComponent] that are nested inside this component.
     */
    public val textInputs: List<TextInputComponent>
        get() = components.filterIsInstance<TextInputComponent>()

    override fun toString(): String = "ActionRowComponent(data=$data)"

}
