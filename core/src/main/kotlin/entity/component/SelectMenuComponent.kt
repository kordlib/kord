package dev.kord.core.entity.component

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.entity.optional.value
import dev.kord.core.cache.data.ComponentData
import dev.kord.core.cache.data.SelectOptionData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.SelectMenuInteraction

/**
 * An interactive dropdown menu rendered on a [Message] that consists of multiple [options].
 */
@KordPreview
class SelectMenuComponent(override val data: ComponentData) : Component {

    /**
     * The custom identifier for any [ComponentInteractions][SelectMenuInteraction]
     * this select menu will trigger.
     */
    val customId: String get() = data.customId.value!!

    /**
     * The placeholder value if no value has been selected, null if not set.
     */
    val placeholder: String? get() = data.placeholder.value

    /**
     * The possible options to choose from.
     */
    val options: List<SelectOption> get() = data.options.orEmpty().map { SelectOption(it) }

    /**
     * The minimum amount of [options] that can be chosen, default `1`.
     */
    val minValues: Int get() = data.minValues.orElse(1)

    /**
     * The maximum amount of [options] that can be chosen, default `1`.
     */
    val maxValues: Int get() = data.maxValues.orElse(1)

    override fun equals(other: Any?): Boolean {
        if (other !is SelectMenuComponent) return false

        return other.data == data
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String = "SelectMenuComponent(data=$data)"
}

/**
 * An option in a [SelectMenuComponent].
 */
class SelectOption(val data: SelectOptionData) {

    /**
     * The user-facing name of the option, max 25 characters.
     */
    val label: String get() = data.label

    /**
     * The dev-define value of the option, max 100 characters.
     */
    val value: String get() = data.value

    /**
     * An additional description of the option, max 50 characters. Null if not set.
     */
    val description: String? get() = data.description.value

    /**
     * The emoji to show in the option. Null if not set.
     */
    val emoji: DiscordPartialEmoji? = data.emoji.value

    /**
     * Whether this option is selected by default.
     */
    val default: Boolean? = data.default.value

    override fun equals(other: Any?): Boolean {
        if (other !is SelectOption) return false

        return other.data == data
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String = "SelectOption(data=$data)"

}
