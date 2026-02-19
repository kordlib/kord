package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordModal
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.ContainerComponentBuilder
import dev.kord.rest.builder.component.LabelComponentBuilder
import dev.kord.rest.builder.component.TextDisplayBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public class ModalBuilder(
    public var title: String,
    public var customId: String
) : RequestBuilder<DiscordModal> {
    public val components: MutableList<ContainerComponentBuilder> = mutableListOf()

    /**
     * Adds an Action Row to the modal, configured by the [builder].
     */
    @Deprecated("[label] is recommended for use over an Action Row in modals. Action Row with Text Inputs " +
            "in modals are now deprecated. The deprecation level will be raised to ERROR in 0.19.0, to HIDDEN in " +
            "0.20.0 and this declaration will be removed in 0.21.0",
        ReplaceWith("label() {}"), DeprecationLevel.WARNING)
    public inline fun actionRow(builder: ActionRowBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ActionRowBuilder().apply(builder))
    }

    /**
     * Adds a Label to the modal, configured by the [builder]
     */
    public inline fun label(label: String, builder: LabelComponentBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(LabelComponentBuilder(label).apply(builder))
    }

    /**
     * Adds a text display to the modal, configured by the [builder]
     */
    public inline fun textDisplay(builder: TextDisplayBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(TextDisplayBuilder().apply(builder))
    }

    override fun toRequest(): DiscordModal = DiscordModal(
        title,
        customId,
        components.map { it.build() }
    )
}
