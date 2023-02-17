package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.*
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.FollowupMessageCreateRequest
import dev.kord.rest.json.request.MultipartFollowupMessageCreateRequest

/**
 * Message builder for creating messages following up interaction responses.
 */
@KordDsl
public class FollowupMessageCreateBuilder(public val ephemeral: Boolean) :
    MessageCreateBuilder,
    RequestBuilder<MultipartFollowupMessageCreateRequest> {

    override var content: String? = null

    override var tts: Boolean? = null

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    override var allowedMentions: AllowedMentionsBuilder? = null


    override val components: MutableList<MessageComponentBuilder> = mutableListOf()

    override val files: MutableList<NamedFile> = mutableListOf()

    override var flags: MessageFlags? = null
    override var suppressEmbeds: Boolean? = null
    override var suppressNotifications: Boolean? = null

    override fun toRequest(): MultipartFollowupMessageCreateRequest {
        return MultipartFollowupMessageCreateRequest(
            FollowupMessageCreateRequest(
                content = Optional(content).coerceToMissing(),
                tts = Optional(tts).coerceToMissing().toPrimitive(),
                embeds = Optional(embeds).mapList { it.toRequest() },
                allowedMentions = Optional(allowedMentions).coerceToMissing().map { it.build() },
                components = Optional(components).coerceToMissing().mapList { it.build() },
                flags = buildMessageFlags(flags, suppressEmbeds, suppressNotifications, ephemeral)
            ),
            files
        )
    }

}
