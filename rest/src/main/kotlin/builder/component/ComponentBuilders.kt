package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordComponent


sealed interface ComponentBuilder {
    fun build(): DiscordComponent
}


sealed interface ActionRowComponentBuilder : ComponentBuilder


sealed interface MessageComponentBuilder : ComponentBuilder
