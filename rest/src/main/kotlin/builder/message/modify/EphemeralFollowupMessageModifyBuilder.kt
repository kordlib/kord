package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.FollowupMessageModifyRequest
import dev.kord.rest.json.request.MultipartFollowupMessageModifyRequest

@KordPreview
class EphemeralFollowupMessageModifyBuilder
    : EphemeralMessageModifyBuilder,
    RequestBuilder<MultipartFollowupMessageModifyRequest> {
    private var state = MessageModifyStateHolder()

    override var content: String? by state::content.delegate()

    override var allowedMentions: AllowedMentionsBuilder? by state::allowedMentions.delegate()

    override var components: MutableList<MessageComponentBuilder>? by state::components.delegate()

    override var embeds: MutableList<EmbedBuilder>? by state::embeds.delegate()

    override fun toRequest(): MultipartFollowupMessageModifyRequest {
        return MultipartFollowupMessageModifyRequest(
            FollowupMessageModifyRequest(
                content = state.content,
                allowedMentions = state.allowedMentions.map { it.build() },
                components = state.components.mapList { it.build() },
                embeds = state.embeds.mapList { it.toRequest() }
            )
        )

    }

}
