package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.DiscordSelectOption
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate

/**
 * A builder for a
 * [Discord Select Option](https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-option-structure).
 *
 * @param label The user-facing name of the option, max 25 characters.
 * @param value The dev-define value of the option, max 100 characters.
 */
@KordDsl
class SelectOptionBuilder(
    var label: String,
    var value: String
) {

    private var _description: Optional<String> = Optional.Missing()

    /**
     * An additional description of the option, max 50 characters.
     */
    var description by ::_description.delegate()

    private var _emoji: Optional<DiscordPartialEmoji> = Optional.Missing()

    /**
     * An emoji to display in the option.
     */
    var emoji: DiscordPartialEmoji? by ::_emoji.delegate()


    private var _default: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Whether this option should be rendered as the default.
     */
    var default: Boolean? by ::_default.delegate()

    fun build(): DiscordSelectOption = DiscordSelectOption(
        label = label,
        value = value,
        description = _description,
        emoji = _emoji,
        default = _default
    )

}
