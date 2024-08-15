package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordModal
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.ActionRowBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public class ModalBuilder(
    public var title: String,
    public var customId: String
) : RequestBuilder<DiscordModal> {
    public val components: MutableList<ActionRowBuilder> = mutableListOf()

    /**
     * Adds an Action Row to the modal, configured by the [builder].
     */
    public inline fun actionRow(builder: ActionRowBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ActionRowBuilder().apply(builder))
    }

    override fun toRequest(): DiscordModal = DiscordModal(
        title,
        customId,
        components.map { it.build() }
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ModalBuilder

        if (title != other.title) return false
        if (customId != other.customId) return false
        if (components != other.components) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + customId.hashCode()
        result = 31 * result + components.hashCode()
        return result
    }

}
