package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.buildMessageFlags
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import dev.kord.rest.json.request.MultipartInteractionResponseCreateRequest

/**
 * Message builder for publicly responding to an interaction.
 */
@KordDsl
public class InteractionResponseCreateBuilder(public val ephemeral: Boolean = false) :
    AbstractMessageCreateBuilder(),
    RequestBuilder<MultipartInteractionResponseCreateRequest> {
    // see https://discord.com/developers/docs/interactions/receiving-and-responding#create-interaction-response
    override fun toRequest(): MultipartInteractionResponseCreateRequest = MultipartInteractionResponseCreateRequest(
        request = InteractionResponseCreateRequest(
            type = InteractionResponseType.ChannelMessageWithSource,
            data = InteractionApplicationCommandCallbackData(
                tts = _tts,
                content = _content,
                embeds = _embeds.mapList { it.toRequest() },
                allowedMentions = _allowedMentions.map { it.build() },
                flags = buildMessageFlags(flags, suppressEmbeds, suppressNotifications, ephemeral),
                components = _components.mapList { it.build() },
                attachments = _attachments.mapList { it.toRequest() },
            ).optional(),
        ),
        files = files.toList(),
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (!super.equals(other)) return false

        other as InteractionResponseCreateBuilder

        return ephemeral == other.ephemeral
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + ephemeral.hashCode()
        return result
    }

}
