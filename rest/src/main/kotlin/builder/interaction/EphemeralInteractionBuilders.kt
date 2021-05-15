package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import dev.kord.rest.json.request.InteractionResponseModifyRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
class EphemeralInteractionResponseModifyBuilder : RequestBuilder<InteractionResponseModifyRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()


    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()


    private var _allowedMentions: Optional<AllowedMentions> = Optional.Missing()
    var allowedMentions: AllowedMentions? by ::_allowedMentions.delegate()


    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(builder: AllowedMentionsBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = AllowedMentionsBuilder().apply(builder).build()
    }


    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }

    override fun toRequest(): InteractionResponseModifyRequest {
        return InteractionResponseModifyRequest(
            content = _content,
            allowedMentions = _allowedMentions,
            embeds = _embeds.mapList { it.toRequest() }
        )

    }
}


@KordPreview
class EphemeralInteractionResponseCreateBuilder : RequestBuilder<InteractionResponseCreateRequest> {
    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()


    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()


    private var _allowedMentions: Optional<AllowedMentions> = Optional.Missing()
    var allowedMentions: AllowedMentions? by ::_allowedMentions.delegate()


    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(builder: AllowedMentionsBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = AllowedMentionsBuilder().apply(builder).build()
    }


    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (embeds == null) embeds = mutableListOf()
        embeds!! += EmbedBuilder().apply(builder)
    }

    override fun toRequest(): InteractionResponseCreateRequest {
        val flags = Optional.Value(MessageFlags(MessageFlag.Ephemeral))

        val type = if (content == null && embeds == null) InteractionResponseType.DeferredChannelMessageWithSource
        else InteractionResponseType.ChannelMessageWithSource
        val data = InteractionApplicationCommandCallbackData(
            content = _content,
            flags = flags,
            embeds = _embeds.mapList { it.toRequest() }
        )
        return InteractionResponseCreateRequest(type, data.optional())

    }
}