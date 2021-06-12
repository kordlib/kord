package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
@KordPreview
class EphemeralInteractionResponseModifyBuilder : BaseInteractionResponseModifyBuilder {
    private var _content: Optional<String> = Optional.Missing()
    override var content: String? by ::_content.delegate()

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    val components: MutableList<MessageComponentBuilder> = mutableListOf()

    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(builder: AllowedMentionsBuilder.() -> Unit) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(builder)
    }

    @OptIn(ExperimentalContracts::class)
    @KordPreview
    inline fun actionRow(builder: ActionRowBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ActionRowBuilder().apply(builder))
    }

    override fun toRequest(): MultipartInteractionResponseModifyRequest {
        return MultipartInteractionResponseModifyRequest(
            InteractionResponseModifyRequest(
                content = _content,
                allowedMentions = _allowedMentions.map { it.build() },
                components = Optional.missingOnEmpty(components.map { it.build() }),
                embeds = embeds.map { it.toRequest() }
            )
        )
    }
}

@KordDsl
@KordPreview
class EphemeralInteractionResponseCreateBuilder : BaseInteractionResponseCreateBuilder {
    private var _content: Optional<String> = Optional.Missing()
    override var content: String? by ::_content.delegate()


    override val embeds: MutableList<EmbedBuilder> = mutableListOf()


    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()


    override fun toRequest(): MultipartInteractionResponseCreateRequest {
        val flags = Optional.Value(MessageFlags(MessageFlag.Ephemeral))

        val type = if (content == null && embeds.isEmpty()) InteractionResponseType.DeferredChannelMessageWithSource
        else InteractionResponseType.ChannelMessageWithSource
        val data = InteractionApplicationCommandCallbackData(
            content = _content,
            flags = flags,
            embeds = Optional.missingOnEmpty(embeds.map { it.toRequest() })
        )
        return MultipartInteractionResponseCreateRequest(
            InteractionResponseCreateRequest(type, data.optional())
        )

    }
}
