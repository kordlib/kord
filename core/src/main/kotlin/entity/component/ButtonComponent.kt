package dev.kord.core.entity.component

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.optional.value
import dev.kord.core.cache.data.ChatComponentData
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.interaction.ComponentInteraction

/**
 * An interactive component rendered on a Message.
 * If this button contains a [customId] and is clicked by a user,
 * a [InteractionCreateEvent] with a [ComponentInteraction] will fire.
 */

public class ButtonComponent(override val data: ChatComponentData) : Component {

    override val type: ComponentType.Button
        get() = ComponentType.Button

    /**
     * The style of this button, [ButtonStyle.Link] buttons will always
     * have a [url].
     */
    public val style: ButtonStyle get() = data.style.value!!

    /**
     * The text that appears on the button, if present.
     */
    public val label: String? get() = data.label.value

    /**
     * The emoji that appears on the button, if present.
     */
    public val emoji: ReactionEmoji?
        get() = with(data.emoji.value) {
            if (this == null) return@with null

            when (id) {
                null -> ReactionEmoji.Unicode(name!!)
                else -> ReactionEmoji.Custom(
                    id!!, name ?: "", animated.value!!
                )
            }
        }

    /**
     * The custom identifier for any [ComponentInteractions][ButtonInteraction]
     * this button will trigger. Present if this button is not a link button.
     */
    public val customId: String? get() = data.customId.value

    /**
     * The url the button will link to. Present if this button is a link button.
     */
    public val url: String? get() = data.url.value

    /**
     * Whether this button can be clicked.
     */
    public val disabled: Boolean get() = data.disabled.discordBoolean

    override fun toString(): String = "ButtonComponent(data=$data)"

}
