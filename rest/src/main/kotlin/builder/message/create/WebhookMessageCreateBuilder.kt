package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.*
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.MultiPartWebhookExecuteRequest
import dev.kord.rest.json.request.WebhookExecuteRequest

/**
 * Message builder for creating messages as a webhook user.
 */
public class WebhookMessageCreateBuilder :
    MessageCreateBuilder,
    RequestBuilder<MultiPartWebhookExecuteRequest> {

    override var content: String? = null

    public var username: String? = null

    public var avatarUrl: String? = null

    override var tts: Boolean? = null

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    override var allowedMentions: AllowedMentionsBuilder? = null


    override val components: MutableList<MessageComponentBuilder> = mutableListOf()

    override val files: MutableList<NamedFile> = mutableListOf()

    @OptIn(KordPreview::class)
    override fun toRequest(): MultiPartWebhookExecuteRequest {
        return MultiPartWebhookExecuteRequest(
            WebhookExecuteRequest(
                content = Optional(content).coerceToMissing(),
                username = Optional(username).coerceToMissing(),
                avatar = Optional(avatarUrl).coerceToMissing(),
                tts = Optional(tts).coerceToMissing().toPrimitive(),
                embeds = Optional(embeds).mapList { it.toRequest() },
                allowedMentions = Optional(allowedMentions).coerceToMissing().map { it.build() },
                components = Optional(components).coerceToMissing().mapList { it.build() }
            ),
            files
        )
    }
}
