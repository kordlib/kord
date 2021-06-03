package dev.kord.rest.builder.components

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordComponent
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
@KordPreview
class ActionRowContainerBuilder {
    val components = mutableListOf<ActionRowBuilder>()

    @OptIn(ExperimentalContracts::class)
    inline fun actionRow(builder: ActionRowBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ActionRowBuilder().apply(builder))
    }

    fun build(): List<DiscordComponent> = components.map(ActionRowBuilder::build)
}
