package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.InteractionResponseModifyRequest
import dev.kord.rest.json.request.MultipartInteractionResponseModifyRequest
import kotlinx.coroutines.Dispatchers

@KordPreview
class EphemeralInteractionResponseModifyBuilder
    : EphemeralMessageModifyBuilder,
    RequestBuilder<MultipartInteractionResponseModifyRequest> {

    private var state = MessageModifyStateHolder()

    override var content: String? by state::content.delegate()

    override var embeds: MutableList<EmbedBuilder>? by state::embeds.delegate()

    override var allowedMentions: AllowedMentionsBuilder? by state::allowedMentions.delegate()

    @KordPreview
    override var components: MutableList<MessageComponentBuilder>? by state::components.delegate()

    override fun toRequest(): MultipartInteractionResponseModifyRequest {
        Dispatchers.Default
        return MultipartInteractionResponseModifyRequest(
            InteractionResponseModifyRequest(
                content = state.content,
                allowedMentions = state.allowedMentions.map { it.build() },
                components = state.components.mapList { it.build() },
                embeds = state.embeds.mapList { it.toRequest() },
                flags = Optional(MessageFlags(MessageFlag.Ephemeral))
            )
        )
    }

}
