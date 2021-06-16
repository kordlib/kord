package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordComponent

@KordPreview
sealed interface ComponentBuilder {
    fun build(): DiscordComponent
}

@KordPreview
sealed interface ActionRowComponentBuilder : ComponentBuilder

@KordPreview
sealed interface MessageComponentBuilder : ComponentBuilder
