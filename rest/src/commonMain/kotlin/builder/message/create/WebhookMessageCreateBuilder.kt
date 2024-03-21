package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ChannelType.GuildForum
import dev.kord.common.entity.ChannelType.GuildMedia
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapCopy
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.buildMessageFlags
import dev.kord.rest.json.request.MultiPartWebhookExecuteRequest
import dev.kord.rest.json.request.WebhookExecuteRequest

/**
 * Message builder for creating messages as a webhook user.
 */
@KordDsl
public class WebhookMessageCreateBuilder :
    AbstractMessageCreateBuilder(),
    RequestBuilder<MultiPartWebhookExecuteRequest> {
    // see https://discord.com/developers/docs/resources/webhook#execute-webhook

    private var _username: Optional<String> = Optional.Missing()

    /** Overrides the default username of the webhook. */
    public var username: String? by ::_username.delegate()

    private var _avatarUrl: Optional<String> = Optional.Missing()

    /** Overrides the default avatar of the webhook. */
    public var avatarUrl: String? by ::_avatarUrl.delegate()

    private var _threadName: Optional<String> = Optional.Missing()

    /** Name of the thread to create (requires the webhook channel to be a [GuildForum] or [GuildMedia] channel). */
    public var threadName: String? by ::_threadName.delegate()

    private var _appliedTags: Optional<MutableList<Snowflake>> = Optional.Missing()

    /**
     * List of tag ids to apply to the thread (requires the webhook channel to be a [GuildForum] or [GuildMedia]
     * channel).
     */
    public var appliedTags: MutableList<Snowflake>? by ::_appliedTags.delegate()

    override fun toRequest(): MultiPartWebhookExecuteRequest = MultiPartWebhookExecuteRequest(
        request = WebhookExecuteRequest(
            content = _content,
            username = _username,
            avatar = _avatarUrl,
            tts = _tts,
            embeds = _embeds.mapList { it.toRequest() },
            allowedMentions = _allowedMentions.map { it.build() },
            components = _components.mapList { it.build() },
            attachments = _attachments.mapList { it.toRequest() },
            flags = buildMessageFlags(flags, suppressEmbeds, suppressNotifications),
            threadName = _threadName,
            appliedTags = _appliedTags.mapCopy(),
            poll = _poll
        ),
        files = files.toList(),
    )
}

/** Add a [tagId] to [appliedTags][WebhookMessageCreateBuilder.appliedTags]. */
public fun WebhookMessageCreateBuilder.applyTag(tagId: Snowflake) {
    appliedTags?.add(tagId) ?: run { appliedTags = mutableListOf(tagId) }
}
