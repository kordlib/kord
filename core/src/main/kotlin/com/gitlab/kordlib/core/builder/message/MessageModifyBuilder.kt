package com.gitlab.kordlib.core.builder.message

import com.gitlab.kordlib.common.entity.Flags
import com.gitlab.kordlib.core.builder.KordDsl
import com.gitlab.kordlib.core.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.MessageEditPatchRequest

@KordDsl
class MessageModifyBuilder : RequestBuilder<MessageEditPatchRequest> {
    var content: String? = null
    var embed: EmbedBuilder? = null
    var flags: Flags? = null

    fun embed(block: EmbedBuilder.() -> Unit) {
        embed = (embed ?: EmbedBuilder()).also(block)
    }

    override fun toRequest(): MessageEditPatchRequest = MessageEditPatchRequest(content, embed?.toRequest())
}