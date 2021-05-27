package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate

@KordPreview
class ButtonBuilder {
    private var _style: Optional<ButtonStyle> = Optional.Missing()
    var style by ::_style.delegate()
    private var _label: Optional<String> = Optional.Missing()
    var label by ::_label.delegate()
    private var _emoji: Optional<DiscordPartialEmoji> = Optional.Missing()
    var emoji by ::_emoji.delegate()
    private var _customId: Optional<String> = Optional.Missing()
    var customId by ::_customId.delegate()
    private var _url: Optional<String> = Optional.Missing()
    var url by ::_url.delegate()
    private var _disabled: OptionalBoolean = OptionalBoolean.Missing
    var disabled by ::_disabled.delegate()

    fun build(): DiscordComponent = DiscordComponent(
        ComponentType.Button, _style, _label, _emoji, _customId, _url, _disabled
    )
}
