package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate

@KordDsl
sealed class ButtonBuilder : ActionRowComponentBuilder {

    /**
     * The text that appears on the button, either this and/or [emoji] need to be set
     * for the button to be valid.
     */
    abstract var label: String?

    /**
     * The emoji that appears on the button, either this and/or [label] need to be set
     * for the button to be valid.
     */
    abstract var emoji: DiscordPartialEmoji?

    /**
     * Whether the button is clickable.
     */
    var disabled: Boolean = false

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

        private var _emoji: Optional<DiscordPartialEmoji> = Optional.Missing()
        override var emoji: DiscordPartialEmoji? by ::_emoji.delegate()

        private var _label: Optional<String> = Optional.Missing()
        override var label: String? by ::_label.delegate()

        override fun build(): DiscordComponent = DiscordComponent(
            ComponentType.Button,
            Optional(style),
            _label,
            _emoji,
            Optional(customId),
            Optional.Missing(),
            OptionalBoolean.Value(disabled),
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

        private var _emoji: Optional<DiscordPartialEmoji> = Optional.Missing()
        override var emoji: DiscordPartialEmoji? by ::_emoji.delegate()

        private var _label: Optional<String> = Optional.Missing()
        override var label: String? by ::_label.delegate()

        override fun build(): DiscordComponent = DiscordComponent(
            ComponentType.Button,
            Optional(ButtonStyle.Link),
            _label,
            _emoji,
            Optional.Missing(),
            Optional.Value(url),
            OptionalBoolean.Value(disabled),
        )

    }

}
