package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.buildMessageFlags
import dev.kord.rest.json.request.FollowupMessageCreateRequest
import dev.kord.rest.json.request.MultipartFollowupMessageCreateRequest

/**
 * Message builder for creating messages following up interaction responses.
 */
@KordDsl
public class FollowupMessageCreateBuilder(public val ephemeral: Boolean) :
    AbstractMessageCreateBuilder(),
    RequestBuilder<MultipartFollowupMessageCreateRequest> {
    // see https://discord.com/developers/docs/interactions/receiving-and-responding#create-followup-message
    override fun toRequest(): MultipartFollowupMessageCreateRequest = MultipartFollowupMessageCreateRequest(
        request = FollowupMessageCreateRequest(
            content = _content,
            tts = _tts,
            embeds = _embeds.mapList { it.toRequest() },
            allowedMentions = _allowedMentions.map { it.build() },
            components = _components.mapList { it.build() },
            attachments = _attachments.mapList { it.toRequest() },
            flags = buildMessageFlags(flags, suppressEmbeds, suppressNotifications, ephemeral),
        ),
        files = files.toList(),
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false

        other as FollowupMessageCreateBuilder

        return ephemeral == other.ephemeral
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + ephemeral.hashCode()
        return result
    }

}
