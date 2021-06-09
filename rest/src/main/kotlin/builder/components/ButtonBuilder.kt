package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.optional
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
@KordPreview
sealed class ButtonBuilder : ActionRowComponentBuilder {

    @PublishedApi
    internal open var _style: Optional<ButtonStyle> = Optional.Missing()
    private var _label: Optional<String> = Optional.Missing()
    var label by ::_label.delegate()
    private var _emoji: Optional<DiscordPartialEmoji> = Optional.Missing()
    var emoji by ::_emoji.delegate()
    internal var _customId: Optional<String> = Optional.Missing()
    internal var _url: Optional<String> = Optional.Missing()
    private var _disabled: OptionalBoolean = OptionalBoolean.Missing
    var disabled by ::_disabled.delegate()


    class InteractionButtonBuilder : ButtonBuilder() {
        var style by ::_style.delegate()
        var customId by ::_customId.delegate()
    }

    class LinkButtonBuilder : ButtonBuilder() {
        var url by ::_url.delegate()
        override var _style: Optional<ButtonStyle> = ButtonStyle.Link.optional()
    }

    override fun build(): DiscordComponent = DiscordComponent(
        ComponentType.Button, _style, _label, _emoji, _customId, _url, _disabled
    )
}
