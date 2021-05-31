package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
class ButtonBuilder {

    @PublishedApi
    internal var _style: Optional<ButtonStyle> = Optional.Missing()
    private var _label: Optional<String> = Optional.Missing()
    var label by ::_label.delegate()
    private var _emoji: Optional<DiscordPartialEmoji> = Optional.Missing()
    var emoji by ::_emoji.delegate()
    private var _customId: Optional<String> = Optional.Missing()
    private var _url: Optional<String> = Optional.Missing()
    private var _disabled: OptionalBoolean = OptionalBoolean.Missing
    var disabled by ::_disabled.delegate()


    inner class StyledButtonBuilder {
        var style by ::_style.delegate()
        var customId by ::_customId.delegate()
    }

    inner class LinkButtonBuilder {
        var url by ::_url.delegate()
    }

    @OptIn(ExperimentalContracts::class)
    inline fun link(builder: LinkButtonBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        LinkButtonBuilder().apply(builder).apply {
            _style = Optional.Value(ButtonStyle.Link)
        }
    }

    @OptIn(ExperimentalContracts::class)
    inline fun styled(builder: StyledButtonBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        StyledButtonBuilder().apply(builder)
    }

    fun build(): DiscordComponent = DiscordComponent(
        ComponentType.Button, _style, _label, _emoji, _customId, _url, _disabled
    )
}
