package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordMessageReference
import dev.kord.common.entity.Permission.ReadMessageHistory
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.buildMessageFlags
import dev.kord.rest.json.request.MessageCreateRequest
import dev.kord.rest.json.request.MultipartMessageCreateRequest

/**
 * Message builder for creating messages as a bot user.
 */
@KordDsl
public class UserMessageCreateBuilder : AbstractMessageCreateBuilder(), RequestBuilder<MultipartMessageCreateRequest> {
    // see https://discord.com/developers/docs/resources/channel#create-message

    private var _nonce: Optional<String> = Optional.Missing()

    /** A value that can be used to verify a message was sent (up to 25 characters). */
    public var nonce: String? by ::_nonce.delegate()

    private var _messageReference: OptionalSnowflake = OptionalSnowflake.Missing

    /**
     * The id of the message being replied to.
     *
     * Requires the [ReadMessageHistory] permission.
     *
     * Replying will not mention the author by default, set [AllowedMentionsBuilder.repliedUser] to `true` via
     * [allowedMentions] to mention the author.
     */
    public var messageReference: Snowflake? by ::_messageReference.delegate()

    private var _failIfNotExists: OptionalBoolean = OptionalBoolean.Missing

    /**
     * Whether to error if the referenced message doesn't exist instead of sending as a normal (non-reply) message,
     * defaults to `true`.
     *
     * This is only useful if combined with [messageReference].
     */
    public var failIfNotExists: Boolean? by ::_failIfNotExists.delegate()

    private var _stickerIds: Optional<MutableList<Snowflake>> = Optional.Missing()

    /** The IDs of up to three stickers to send in the message. */
    public var stickerIds: MutableList<Snowflake>? by ::_stickerIds.delegate()

    override fun toRequest(): MultipartMessageCreateRequest = MultipartMessageCreateRequest(
        request = MessageCreateRequest(
            content = _content,
            nonce = _nonce,
            tts = _tts,
            embeds = _embeds.mapList { it.toRequest() },
            allowedMentions = _allowedMentions.map { it.build() },
            messageReference = when (val id = _messageReference) {
                is OptionalSnowflake.Value ->
                    Optional.Value(DiscordMessageReference(id = id, failIfNotExists = _failIfNotExists))
                is OptionalSnowflake.Missing -> Optional.Missing()
            },
            components = _components.mapList { it.build() },
            stickerIds = _stickerIds.mapCopy(),
            attachments = _attachments.mapList { it.toRequest() },
            flags = buildMessageFlags(flags, suppressEmbeds, suppressNotifications),
            poll = _poll
        ),
        files = files.toList(),
    )
}

/** Add a [stickerId] to [stickerIds][UserMessageCreateBuilder.stickerIds]. */
public fun UserMessageCreateBuilder.stickerId(stickerId: Snowflake) {
    stickerIds?.add(stickerId) ?: run { stickerIds = mutableListOf(stickerId) }
}
