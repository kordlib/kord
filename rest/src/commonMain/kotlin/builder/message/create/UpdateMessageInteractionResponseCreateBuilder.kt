package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.*
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import dev.kord.rest.json.request.MultipartInteractionResponseCreateRequest

@KordDsl
public class UpdateMessageInteractionResponseCreateBuilder :
    MessageCreateBuilder,
    RequestBuilder<MultipartInteractionResponseCreateRequest> {


    override var files: MutableList<NamedFile> = mutableListOf()

    override var content: String? = null

    override var tts: Boolean? = null

    override var embeds: MutableList<EmbedBuilder> = mutableListOf()

    override var allowedMentions: AllowedMentionsBuilder? = null

    override var components: MutableList<MessageComponentBuilder> = mutableListOf()

    override var flags: MessageFlags? = null
    override var suppressEmbeds: Boolean? = null
    override var suppressNotifications: Boolean? = null

    override fun toRequest(): MultipartInteractionResponseCreateRequest {
        return MultipartInteractionResponseCreateRequest(
            InteractionResponseCreateRequest(
                InteractionResponseType.UpdateMessage,
                InteractionApplicationCommandCallbackData(
                    content = Optional(content).coerceToMissing(),
                    embeds = Optional(embeds).mapList { it.toRequest() },
                    allowedMentions = Optional(allowedMentions).map { it.build() },
                    components = Optional(components).mapList { it.build() },
                    tts = Optional(tts).coerceToMissing().toPrimitive(),
                    flags = buildMessageFlags(flags, suppressEmbeds, suppressNotifications)
                ).optional()
            ),
            Optional(files)
        )

    }
}
