package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.MultipartInteractionResponseCreateRequest
import dev.kord.rest.json.request.MultipartInteractionResponseModifyRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed interface BaseInteractionResponseBuilder<T> : RequestBuilder<T> {

    var content: String?

    val embeds: MutableList<EmbedBuilder>?

    val components: MutableList<MessageComponentBuilder>?

    var allowedMentions: AllowedMentionsBuilder?


}

@KordPreview
@OptIn(ExperimentalContracts::class, KordPreview::class)
inline fun <T> BaseInteractionResponseBuilder<T>.embed(builder: EmbedBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    when(this){
        is BaseInteractionResponseCreateBuilder -> {
            embeds.add(EmbedBuilder().apply(builder))
        }
        is BaseInteractionResponseModifyBuilder -> {
            embeds = (embeds ?: mutableListOf()).also {
                it.add(EmbedBuilder().apply(builder))
            }
        }
    }
}

/**
 * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
 * (ping everything), calling this function but not configuring it before the request is build will result in all
 * pings being ignored.
 */
@KordPreview
@OptIn(ExperimentalContracts::class)
inline fun <T> BaseInteractionResponseBuilder<T>.allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    if (allowedMentions == null) allowedMentions = AllowedMentionsBuilder()
    allowedMentions!!.apply(block)
}


@OptIn(ExperimentalContracts::class)
@KordPreview
inline fun <T> BaseInteractionResponseBuilder<T>.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    when (this) {
        is BaseInteractionResponseCreateBuilder -> {
            components.add(ActionRowBuilder().apply(builder))
        }
        is BaseInteractionResponseModifyBuilder -> {
            components = (components ?: mutableListOf()).also {
                it.add(ActionRowBuilder().apply(builder))
            }
        }
    }

}


@KordPreview
interface BaseInteractionResponseCreateBuilder :
    BaseInteractionResponseBuilder<MultipartInteractionResponseCreateRequest> {

    override val components: MutableList<MessageComponentBuilder>

    override val embeds: MutableList<EmbedBuilder>

}

@KordPreview
interface BaseInteractionResponseModifyBuilder :
    BaseInteractionResponseBuilder<MultipartInteractionResponseModifyRequest> {

    override var components: MutableList<MessageComponentBuilder>?

    override var embeds: MutableList<EmbedBuilder>?

}
