package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordPreview

@KordPreview
interface CompositeButtonBuilder {
    fun button(builder: ButtonBuilder.() -> Unit)
}
