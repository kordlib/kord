package dev.kord.core.entity.component

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.cache.data.ComponentData

/**
 * A non-interactive container component for other types of component.
 */
@KordPreview
class ActionRowComponent(override val data: ComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.ActionRow


    /**
     * The buttons that are nested inside this component
     */
    val buttons: List<ButtonComponent>
        get() = data.components.orEmpty()
            .filter { it.type == ComponentType.Button }
            .map { ButtonComponent(it) }

    override fun toString(): String = "ActionRowComponent(data=$data)"

}
