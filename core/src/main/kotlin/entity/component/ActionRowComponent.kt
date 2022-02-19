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
     * All [Component]s that are nested inside this action row.
     */
    public val components: List<Component>
        get() = data.components.orEmpty().map { Component(it) }

    /**
     * The [ButtonComponent]s of this action row that are not a link button, indexed by their
     * [customId][ButtonComponent.customId] (which is always present on these buttons).
     *
     * @see components
     */
    public val interactionButtons: Map<String, ButtonComponent>
        get() = components.filterIsInstance<ButtonComponent>()
            .filter { it.customId != null }
            .associateBy { it.customId!! }

    /**
     * The [ButtonComponent]s of this action row that are a link button. [url][ButtonComponent.url] is always present on
     * these buttons.
     *
     * @see components
     */
    public val linkButtons: List<ButtonComponent>
        get() = components.filterIsInstance<ButtonComponent>().filter { it.url != null }

    /**
     * The [SelectMenuComponent]s of this action row, indexed by their [customId][SelectMenuComponent.customId].
     *
     * @see components
     */
    public val selectMenus: Map<String, SelectMenuComponent>
        get() = components.filterIsInstance<SelectMenuComponent>().associateBy { it.customId }

    /**
     * The [TextInputComponent]s of this action row, indexed by their [customId][TextInputComponent.customId].
     *
     * @see components
     */
    public val textInputs: Map<String, TextInputComponent>
        get() = components.filterIsInstance<TextInputComponent>().associateBy { it.customId }

    override fun toString(): String = "ActionRowComponent(data=$data)"

}
