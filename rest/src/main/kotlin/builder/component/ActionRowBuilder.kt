package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
@KordPreview
class ActionRowBuilder : MessageComponentBuilder {
    val components = mutableListOf<ActionRowComponentBuilder>()

    @OptIn(ExperimentalContracts::class)
    inline fun interactionButton(
        style: ButtonStyle,
        customId: String,
        builder: ButtonBuilder.InteractionButtonBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(
            ButtonBuilder.InteractionButtonBuilder(style, customId).apply(builder)
        )
    }

    @OptIn(ExperimentalContracts::class)
    inline fun linkButton(
        url: String,
        builder: ButtonBuilder.LinkButtonBuilder.() -> Unit
    ) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(
            ButtonBuilder.LinkButtonBuilder(url).apply(builder)
        )
    }

    override fun build(): DiscordComponent =
        DiscordComponent(
            ComponentType.ActionRow,
            components = Optional.missingOnEmpty(components.map(ActionRowComponentBuilder::build))
        )
}
