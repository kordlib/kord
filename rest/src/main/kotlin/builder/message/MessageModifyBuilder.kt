package dev.kord.rest.builder.message

import dev.kord.common.entity.UserFlags
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapNullable
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.MessageEditPatchRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
class MessageModifyBuilder : RequestBuilder<MessageEditPatchRequest> {

    private var _content: Optional<String?> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _embed: Optional<EmbedBuilder?> = Optional.Missing()
    var embed: EmbedBuilder? by ::_embed.delegate()

    private var _flags: Optional<MessageFlags?> = Optional.Missing()
    var flags: MessageFlags? by ::_flags.delegate()

    private var _allowedMentions: Optional<AllowedMentionsBuilder?> = Optional.Missing()
    var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    @OptIn(ExperimentalContracts::class)
    inline fun embed(block: EmbedBuilder.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        embed = (embed ?: EmbedBuilder()).also(block)
    }

    /**
     * Configures the mentions that should trigger a ping. Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    @OptIn(ExperimentalContracts::class)
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }


    override fun toRequest(): MessageEditPatchRequest = MessageEditPatchRequest(
        _content, _embed.mapNullable { it?.toRequest() }, _flags, _allowedMentions.mapNullable { it?.build() }
    )
}
