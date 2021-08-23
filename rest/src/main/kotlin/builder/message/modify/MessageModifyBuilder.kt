package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import java.io.InputStream
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed interface MessageModifyBuilder {

    var content: String?

    var embeds: MutableList<EmbedBuilder>?

    var allowedMentions: AllowedMentionsBuilder?

    @OptIn(KordPreview::class)
    var components: MutableList<MessageComponentBuilder>?

}

@OptIn(ExperimentalContracts::class)
inline fun MessageModifyBuilder.embed(block: EmbedBuilder.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    embeds = (embeds ?: mutableListOf()).also {
        it.add(EmbedBuilder().apply(block))
    }
}

/**
 * Configures the mentions that should trigger a ping. Not calling this function will result in the default behavior
 * (ping everything), calling this function but not configuring it before the request is build will result in all
 * pings being ignored.
 */
@OptIn(ExperimentalContracts::class)
inline fun MessageModifyBuilder.allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
}


@OptIn(ExperimentalContracts::class)
inline fun MessageModifyBuilder.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    components = (components ?: mutableListOf()).also {
        it.add(ActionRowBuilder().apply(builder))
    }
}
