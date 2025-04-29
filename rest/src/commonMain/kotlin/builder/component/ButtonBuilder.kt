@file:Suppress("PropertyName")

package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.optionalSnowflake

@KordDsl
public sealed class ButtonBuilder : AccessoryComponentBuilder, ActionRowComponentBuilder() {

    protected var _label: Optional<String> = Optional.Missing()
        private set

    /**
     * The text that appears on the button, either this and/or [emoji] need to be set
     * for the button to be valid.
     */
    public var label: String? by ::_label.delegate()

    protected var _emoji: Optional<DiscordPartialEmoji> = Optional.Missing()
        private set

    /**
     * The emoji that appears on the button, either this and/or [label] need to be set
     * for the button to be valid.
     */
    public var emoji: DiscordPartialEmoji? by ::_emoji.delegate()

    abstract override fun build(): DiscordChatComponent

    /**
     * A builder for a button that can create Interactions when clicked.
     *
     * @param style the style of this button, [ButtonStyle.Link] is not valid.
     * @param customId the ID of this button, used to identify component interactions.
     */
    @KordDsl
    public class InteractionButtonBuilder(
        public var style: ButtonStyle,
        public var customId: String,
    ) : ButtonBuilder() {
        override fun build(): DiscordChatComponent = DiscordChatComponent(
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
    @KordDsl
    public class LinkButtonBuilder(
        public var url: String,
    ) : ButtonBuilder() {
        override fun build(): DiscordChatComponent = DiscordChatComponent(
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


/**
 * A button that prompts the user to purchase an [SKU][DiscordSku].
 *
 * @param skuId The ID of the [SKU][DiscordSku] to prompt for purchase when clicked.
 */
@KordDsl
public class PremiumButtonBuilder(
    public var skuId: Snowflake,
) : ActionRowComponentBuilder() {
    override fun build(): DiscordChatComponent = DiscordChatComponent(
        type = ComponentType.Button,
        style = Optional(ButtonStyle.Premium),
        skuId = skuId.optionalSnowflake(),
        disabled = _disabled,
    )
}
