package dev.kord.rest.builder.message

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapList
import dev.kord.common.entity.optional.mapNullable
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.json.request.MessageEditPatchRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
class MessageModifyBuilder : RequestBuilder<MessageEditPatchRequest> {

    private var _content: Optional<String?> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _flags: Optional<MessageFlags?> = Optional.Missing()
    var flags: MessageFlags? by ::_flags.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder?> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    @OptIn(KordPreview::class)
    private var _components: Optional<MutableList<MessageComponentBuilder>> = Optional.Missing()

    @KordPreview
    var components: MutableList<MessageComponentBuilder>? by ::_components.delegate()

    @OptIn(ExperimentalContracts::class)
    inline fun embed(block: EmbedBuilder.() -> Unit) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        if(embeds == null) embeds = mutableListOf()
        embeds!!.add(EmbedBuilder().apply(block))
    }

    /**
     * Configures the mentions that should trigger a ping. Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }

    @OptIn(ExperimentalContracts::class)
    @KordPreview
    inline fun actionRow(builder: ActionRowBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        components = (components ?: mutableListOf()).also {
            it.add(ActionRowBuilder().apply(builder))
        }
    }

    @OptIn(KordPreview::class)
    override fun toRequest(): MessageEditPatchRequest = MessageEditPatchRequest(
        _content,
        _embeds.mapList { it.toRequest() },
        _flags,
        _allowedMentions.mapNullable { it?.build() },
        _components.mapList { it.build() }
    )
}
