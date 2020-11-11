package com.gitlab.kordlib.rest.builder.message

import com.gitlab.kordlib.common.entity.UserFlags
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.MessageEditPatchRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
class MessageModifyBuilder : RequestBuilder<MessageEditPatchRequest> {
    var content: String? = null
    var embed: EmbedBuilder? = null
    var flags: UserFlags? = null
    var allowedMentions: AllowedMentionsBuilder? = null

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


    override fun toRequest(): MessageEditPatchRequest = MessageEditPatchRequest(content, embed?.toRequest(), flags, allowedMentions?.build())
}
