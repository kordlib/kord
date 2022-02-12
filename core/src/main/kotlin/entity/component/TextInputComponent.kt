package dev.kord.core.entity.component

import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.TextInputStyle
import dev.kord.common.entity.optional.value
import dev.kord.core.cache.data.TextInputComponentData
import dev.kord.core.entity.interaction.ComponentInteraction

/**
 * An interactive component rendered on a Message.
 * If this button contains a [customId] and is clicked by a user,
 * a [InteractionCreateEvent] with a [ComponentInteraction] will fire.
 */

public class TextInputComponent(override val data: TextInputComponentData) : Component {

    override val type: ComponentType
        get() = ComponentType.TextInput

    /**
     * The style of this text input.
     */
    public val style: TextInputStyle get() = data.style.value!!

    /**
     * The text that appears on the button, if present.
     */
    public val label: String? get() = data.label.value

    /**
     * The custom identifier for this Text Input.
     */
    public val customId: String get() = data.customId.value!!

    /**
     * The minimum text length of the text input, if present.
     */
    public val minLength: Int? get() = data.minLength.value

    /**
     * The maximum text length of the text input, if present.
     */
    public val maxLength: Int? get() = data.maxLength.value

    /**
     * If the text input is required.
     */
    public val required: Boolean get() = data.required.discordBoolean

    /**
     * The value of the text input.
     */
    public val value: String? get() = data.value.value

    /**
     * The placeholder text of the text input.
     */
    public val placeholder: String? get() = data.placeholder.value

    override fun toString(): String = "TextInputComponent(data=$data)"

}
