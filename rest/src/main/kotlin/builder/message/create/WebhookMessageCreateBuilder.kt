package dev.kord.rest.builder.message.create

import dev.kord.common.entity.ChannelType.GuildForum
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
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

    private var _threadName: Optional<String> = Optional.Missing()

    /** Name of the thread to create (requires the webhook channel to be a [GuildForum] channel). */
    public var threadName: String? by ::_threadName.delegate()

    override fun toRequest(): MultiPartWebhookExecuteRequest {
        return MultiPartWebhookExecuteRequest(
            WebhookExecuteRequest(
                content = Optional(content).coerceToMissing(),
                username = Optional(username).coerceToMissing(),
                avatar = Optional(avatarUrl).coerceToMissing(),
                tts = Optional(tts).coerceToMissing().toPrimitive(),
                embeds = Optional(embeds).mapList { it.toRequest() },
                allowedMentions = Optional(allowedMentions).coerceToMissing().map { it.build() },
                components = Optional(components).coerceToMissing().mapList { it.build() },
                threadName = _threadName,
            ),
            files
        )
    }
}
