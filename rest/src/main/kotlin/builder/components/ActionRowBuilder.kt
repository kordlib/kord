package dev.kord.rest.builder.components

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
class ActionRowBuilder {
    val components = mutableListOf<DiscordComponent>()

    @OptIn(ExperimentalContracts::class)
    inline fun interactionButton(style: ButtonStyle, customId: String, builder: ButtonBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ButtonBuilder.InteractionButtonBuilder()
            .apply {
                this.style = style
                this.customId = customId
            }
            .apply(builder).build())
    }

    @OptIn(ExperimentalContracts::class)
    inline fun linkButton(url: String, builder: ButtonBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ButtonBuilder.LinkButtonBuilder()
            .apply { this.url = url }
            .apply(builder).build())
    }

    fun build(): DiscordComponent = DiscordComponent(ComponentType.ActionRow, components = Optional.missingOnEmpty(components))
}
