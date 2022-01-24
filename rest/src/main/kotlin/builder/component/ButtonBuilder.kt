@file:Suppress("PropertyName")

package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate

@KordDsl
sealed class ButtonBuilder : ActionRowComponentBuilder() {

    protected var _label: Optional<String> = Optional.Missing()
        private set

    /**
     * The text that appears on the button, either this and/or [emoji] need to be set
     * for the button to be valid.
     */
    var label: String? by ::_label.delegate()

    protected var _emoji: Optional<DiscordPartialEmoji> = Optional.Missing()
        private set

    /**
     * The emoji that appears on the button, either this and/or [label] need to be set
     * for the button to be valid.
     */
    var emoji: DiscordPartialEmoji? by ::_emoji.delegate()

    /**
     * A builder for a button that can create Interactions when clicked.
     *
     * @param style the style of this button, [ButtonStyle.Link] is not valid.
     * @param customId the ID of this button, used to identify component interactions.
     */
    class InteractionButtonBuilder(
        var style: ButtonStyle,
        var customId: String
    ) : ButtonBuilder() {
        override fun build(): DiscordComponent = DiscordComponent(
            ComponentType.Button,
            Optional(style),
            _label,
            _emoji,
            Optional(customId),
            Optional.Missing(),
            _disabled,
        )
    }

    /**
     * A button that links to the [url].
     *
     * @param url The url to open when clicked.
     */
    class LinkButtonBuilder(
        var url: String
    ) : ButtonBuilder() {
        override fun build(): DiscordComponent = DiscordComponent(
            ComponentType.Button,
            Optional(ButtonStyle.Link),
            _label,
            _emoji,
            Optional.Missing(),
            Optional.Value(url),
            _disabled,
        )
    }
}
