package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapCopy
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.buildMessageFlags
import dev.kord.rest.json.request.ForumThreadMessageRequest
import dev.kord.rest.json.request.MultipartForumThreadMessageCreateRequest

@KordDsl
public class ForumMessageCreateBuilder :
    AbstractMessageCreateBuilder(),
    RequestBuilder<MultipartForumThreadMessageCreateRequest> {
    // see https://discord.com/developers/docs/resources/channel#start-thread-in-forum-or-media-channel

    private var _stickerIds: Optional<MutableList<Snowflake>> = Optional.Missing()

    /** The IDs of up to three stickers to send in the message. */
    public var stickerIds: MutableList<Snowflake>? by ::_stickerIds.delegate()

    override fun toRequest(): MultipartForumThreadMessageCreateRequest = MultipartForumThreadMessageCreateRequest(
        request = ForumThreadMessageRequest(
            content = _content,
            tts = _tts,
            embeds = _embeds.mapList { it.toRequest() },
            allowedMentions = _allowedMentions.map { it.build() },
            components = _components.mapList { it.build() },
            stickerIds = _stickerIds.mapCopy(),
            attachments = _attachments.mapList { it.toRequest() },
            flags = buildMessageFlags(flags, suppressEmbeds, suppressNotifications),
            poll = _poll
        ),
        files = files.toList(),
    )
}

/** Add a [stickerId] to [stickerIds][ForumMessageCreateBuilder.stickerIds]. */
public fun ForumMessageCreateBuilder.stickerId(stickerId: Snowflake) {
    stickerIds?.add(stickerId) ?: run { stickerIds = mutableListOf(stickerId) }
}
