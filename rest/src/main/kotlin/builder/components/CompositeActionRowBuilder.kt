package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordPreview

@KordPreview
interface CompositeActionRowBuilder {
    fun actionRow(builder: CompositeButtonBuilder.() -> Unit)
}
