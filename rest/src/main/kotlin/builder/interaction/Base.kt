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

    val embeds: MutableList<EmbedBuilder>

    val components: MutableList<MessageComponentBuilder>

    var allowedMentions: AllowedMentionsBuilder?


}

@OptIn(ExperimentalContracts::class)
inline fun <T> BaseInteractionResponseBuilder<T>.embed(builder: EmbedBuilder.() -> Unit) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    embeds += EmbedBuilder().apply(builder)
}

/**
 * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
 * (ping everything), calling this function but not configuring it before the request is build will result in all
 * pings being ignored.
 */
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

    components.add(ActionRowBuilder().apply(builder))
}


@KordPreview
interface BaseInteractionResponseCreateBuilder :
    BaseInteractionResponseBuilder<MultipartInteractionResponseCreateRequest>

@KordPreview
interface BaseInteractionResponseModifyBuilder :
    BaseInteractionResponseBuilder<MultipartInteractionResponseModifyRequest>
