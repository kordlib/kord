package dev.kord.core.entity.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.ComponentData

/**
 * A non-interactive container component for other types of component.
 */
public class ActionRowComponent(override val data: ComponentData) : Component {

    override val type: ComponentType.ActionRow
        get() = ComponentType.ActionRow

    /**
     * All [Component]s that are nested inside this component.
     */
    public val components: List<Component>
        get() = data.components.orEmpty().map { Component(it) }

    /**
     * The [ButtonComponent]s that are nested inside this component.
     * @see components
     */
    public val buttons: List<ButtonComponent>
        get() = components.filterIsInstance<ButtonComponent>()

    /**
     * The [SelectMenuComponent]s that are nested inside this component.
     * @see components
     */
    public val selectMenus: List<SelectMenuComponent>
        get() = components.filterIsInstance<SelectMenuComponent>()

    /**
     * The [TextInputComponent]s that are nested inside this component.
     * @see components
     */
    public val textInputs: List<TextInputComponent>
        get() = components.filterIsInstance<TextInputComponent>()

    override fun toString(): String = "ActionRowComponent(data=$data)"

}
