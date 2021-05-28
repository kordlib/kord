package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordComponent

@KordPreview
interface ComponentBuilder {
    fun toComponent(): DiscordComponent
}
