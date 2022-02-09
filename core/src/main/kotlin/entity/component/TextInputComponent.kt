package dev.kord.core.entity.component

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.TextInputStyle
import dev.kord.common.entity.optional.value
import dev.kord.core.cache.data.ButtonComponentData
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.cache.data.TextInputComponentData
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.interaction.ComponentInteraction

/**
 * An text input component rendered on a modal.
 */

public class TextInputComponent(override val data: TextInputComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.TextInput

    /**
     * The style of this text input.
     */
    public val style: TextInputStyle get() = data.style

    /**
     * The custom ID of this text input, if present.
     */
    public val customId: String get() = data.customId

    /**
     * The label of the text input
     */
    public val label: String get() = data.label

    override fun toString(): String = "TextInputComponent(data=$data)"

}
