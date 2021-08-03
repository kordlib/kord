package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.*
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.MultiPartWebhookExecuteRequest
import dev.kord.rest.json.request.WebhookExecuteRequest
import java.io.InputStream

/**
 * Message builder for creating messages as a webhook user.
 */
class WebhookMessageCreateBuilder
    : PersistentMessageCreateBuilder,
    RequestBuilder<MultiPartWebhookExecuteRequest> {

    override var content: String? = null

    override var tts: Boolean? = null

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    override var allowedMentions: AllowedMentionsBuilder? = null

    @KordPreview
    override val components: MutableList<MessageComponentBuilder> = mutableListOf()

    override val files: MutableList<Pair<String, InputStream>> = mutableListOf()

    @OptIn(KordPreview::class)
    override fun toRequest(): MultiPartWebhookExecuteRequest {
        return MultiPartWebhookExecuteRequest(
            WebhookExecuteRequest(
                content = Optional(content).coerceToMissing(),
                tts = Optional(tts).coerceToMissing().toPrimitive(),
                embeds = Optional(embeds).mapList { it.toRequest() },
                allowedMentions = Optional(allowedMentions).coerceToMissing().map { it.build() },
                components = Optional(components).coerceToMissing().mapList { it.build() }
            ),
            files
        )
    }
}
