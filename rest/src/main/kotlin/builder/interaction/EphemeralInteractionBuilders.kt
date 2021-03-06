package dev.kord.rest.builder.interaction

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.component.ComponentBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.*

@KordDsl
@KordPreview
class EphemeralInteractionResponseModifyBuilder : BaseInteractionResponseModifyBuilder {
    private var _content: Optional<String?> = Optional.Missing()
    override var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    override var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder?> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    private var _components: Optional<MutableList<MessageComponentBuilder>> = Optional.Missing()
    override var components: MutableList<MessageComponentBuilder>? by ::_components.delegate()

    override fun toRequest(): MultipartInteractionResponseModifyRequest {
        return MultipartInteractionResponseModifyRequest(
            InteractionResponseModifyRequest(
                content = _content,
                allowedMentions = _allowedMentions.map { it.build() },
                components = _components.mapList { it.build() },
                embeds = _embeds.mapList {  it.toRequest() },
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

    override val components: MutableList<MessageComponentBuilder> = mutableListOf()


    private var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()


    override fun toRequest(): MultipartInteractionResponseCreateRequest {
        val flags = Optional.Value(MessageFlags(MessageFlag.Ephemeral))

        val type = if (content == null && embeds.isEmpty()) InteractionResponseType.DeferredChannelMessageWithSource
        else InteractionResponseType.ChannelMessageWithSource
        val data = InteractionApplicationCommandCallbackData(
            content = _content,
            flags = flags,
            embeds = Optional.missingOnEmpty(embeds.map(EmbedBuilder::toRequest)),
            components = Optional.missingOnEmpty(components.map(ComponentBuilder::build))
        )

        return MultipartInteractionResponseCreateRequest(
            InteractionResponseCreateRequest(type, data.optional())
        )

    }
}
