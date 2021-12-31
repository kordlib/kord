package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordComponent

@KordDsl
public sealed interface ComponentBuilder {
    public fun build(): DiscordComponent
}

@KordDsl
public sealed interface ActionRowComponentBuilder : ComponentBuilder

@KordDsl
public sealed interface MessageComponentBuilder : ComponentBuilder
