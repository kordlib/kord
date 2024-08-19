package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.DiscordSelectOption
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate

/**
 * A builder for a
 * [Discord Select Option](https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-option-structure).
 *
 * @param label The user-facing name of the option, max 100 characters.
 * @param value The dev-defined value of the option, max 100 characters.
 */
@KordDsl
public class SelectOptionBuilder(
    public var label: String,
    public var value: String,
) {

    private var _description: Optional<String> = Optional.Missing()

    /**
     * An additional description of the option, max 100 characters.
     */
    public var description: String? by ::_description.delegate()

    private var _emoji: Optional<DiscordPartialEmoji> = Optional.Missing()

    /**
     * An emoji to display in the option.
     */
    public var emoji: DiscordPartialEmoji? by ::_emoji.delegate()


    private var _default: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Whether this option should be rendered as the default.
     */
    public var default: Boolean? by ::_default.delegate()

    public fun build(): DiscordSelectOption = DiscordSelectOption(
        label = label,
        value = value,
        description = _description,
        emoji = _emoji,
        default = _default
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SelectOptionBuilder

        if (label != other.label) return false
        if (value != other.value) return false
        if (description != other.description) return false
        if (emoji != other.emoji) return false
        if (default != other.default) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (emoji?.hashCode() ?: 0)
        result = 31 * result + (default?.hashCode() ?: 0)
        return result
    }

}
