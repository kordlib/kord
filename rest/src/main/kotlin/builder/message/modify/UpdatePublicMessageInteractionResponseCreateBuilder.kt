package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionResponseType
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.optional
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.InteractionApplicationCommandCallbackData
import dev.kord.rest.json.request.InteractionResponseCreateRequest
import dev.kord.rest.json.request.MultipartInteractionResponseCreateRequest
import java.io.InputStream

@KordPreview
class UpdatePublicMessageInteractionResponseCreateBuilder :
    PersistentMessageModifyBuilder,
    RequestBuilder<MultipartInteractionResponseCreateRequest> {

    private var state = MessageModifyStateHolder()

    override var files: MutableList<Pair<String, InputStream>>? by state::files.delegate()

    override var content: String? by state::content.delegate()

    override var embeds: MutableList<EmbedBuilder>? by state::embeds.delegate()

    var flags: MessageFlags? by state::flags.delegate()

    override var allowedMentions: AllowedMentionsBuilder? by state::allowedMentions.delegate()

    override var components: MutableList<MessageComponentBuilder>? by state::components.delegate()

    override fun toRequest(): MultipartInteractionResponseCreateRequest {
        return MultipartInteractionResponseCreateRequest(
            InteractionResponseCreateRequest(
                InteractionResponseType.UpdateMessage,
                InteractionApplicationCommandCallbackData(
                    content = state.content,
                    embeds = state.embeds.mapList { it.toRequest() },
                    allowedMentions = state.allowedMentions.map { it.build() },
                    flags = state.flags.coerceToMissing(),
                    components = state.components.mapList { it.build() }
                ).optional()
            ),
            state.files
        )

    }

}
