package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.ForumThreadMessageRequest
import dev.kord.rest.json.request.MultipartForumThreadMessageCreateRequest

@KordDsl
public class ForumMessageCreateBuilder : MessageCreateBuilder,
    RequestBuilder<MultipartForumThreadMessageCreateRequest> {

    override var content: String? = null

    override var tts: Boolean? = null

    override val embeds: MutableList<EmbedBuilder> = mutableListOf()

    override var allowedMentions: AllowedMentionsBuilder? = null

    override val components: MutableList<MessageComponentBuilder> = mutableListOf()

    override val files: MutableList<NamedFile> = mutableListOf()

    private var _stickerIds: Optional<MutableList<Snowflake>> = Optional.Missing()
    public val stickerIds: MutableList<Snowflake>? by ::_stickerIds.delegate()

    override var flags: MessageFlags? = null
    override var suppressEmbeds: Boolean? = null
    override var suppressNotifications: Boolean? = null

    override fun toRequest(): MultipartForumThreadMessageCreateRequest {
        return MultipartForumThreadMessageCreateRequest(
            ForumThreadMessageRequest(
                content = Optional(content).coerceToMissing(),
                embeds = Optional(embeds).mapList { it.toRequest() },
                allowedMentions = Optional(allowedMentions).coerceToMissing().map { it.build() },
                components = Optional(components).coerceToMissing().mapList { it.build() },
                stickerIds = _stickerIds,
                flags = buildMessageFlags(flags, suppressEmbeds, suppressNotifications),
            ),
            files
        )
    }
}
