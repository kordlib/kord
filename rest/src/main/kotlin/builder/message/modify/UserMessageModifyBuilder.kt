package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.create.buildMessageFlags
import dev.kord.rest.json.request.MessageEditPatchRequest
import dev.kord.rest.json.request.MultipartMessagePatchRequest

@KordDsl
public class UserMessageModifyBuilder
    : MessageModifyBuilder,
    RequestBuilder<MultipartMessagePatchRequest> {

    private var state = MessageModifyStateHolder()

    override var files: MutableList<NamedFile>? by state::files.delegate()

    override var attachments: MutableList<DiscordAttachment>? by state::attachments.delegate()

    override var content: String? by state::content.delegate()

    override var embeds: MutableList<EmbedBuilder>? by state::embeds.delegate()

    override var flags: MessageFlags? = null
    override var suppressEmbeds: Boolean? = null

    override var allowedMentions: AllowedMentionsBuilder? by state::allowedMentions.delegate()


    override var components: MutableList<MessageComponentBuilder>? by state::components.delegate()

    override fun toRequest(): MultipartMessagePatchRequest = MultipartMessagePatchRequest(
        MessageEditPatchRequest(
            content = state.content,
            embeds = state.embeds.mapList { it.toRequest() },
            flags = buildMessageFlags(flags, suppressEmbeds),
            allowedMentions = state.allowedMentions.map { it.build() },
            components = state.components.mapList { it.build() },
            attachments = state.attachments,
        ),
        state.files,
    )

}
