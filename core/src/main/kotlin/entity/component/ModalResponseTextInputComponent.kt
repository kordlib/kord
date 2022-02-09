package dev.kord.core.entity.component

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.TextInputStyle
import dev.kord.common.entity.optional.value
import dev.kord.core.cache.data.ButtonComponentData
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.cache.data.ModalResponseTextInputComponentData
import dev.kord.core.cache.data.TextInputComponentData
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.interaction.ComponentInteraction

/**
 * An text input component rendered on a modal.
 */

public class ModalResponseTextInputComponent(override val data: ModalResponseTextInputComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.TextInput

    /**
     * The custom ID of this text input, if present.
     */
    public val customId: String get() = data.customId

    /**
     * The value that appears on the text input.
     */
    public val value: String get() = data.value

    override fun toString(): String = "TextInputComponent(data=$data)"

}
