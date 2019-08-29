package com.gitlab.kordlib.core.builder.message

import com.gitlab.kordlib.core.builder.KordBuilder
import com.gitlab.kordlib.core.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.MessageEditRequest

@KordBuilder
class MessageModifyBuilder : RequestBuilder<MessageEditRequest> {
    var content: String? = null
    var embed: EmbedBuilder? = null

    fun embed(block: EmbedBuilder.() -> Unit) {
        embed = (embed ?: EmbedBuilder()).also(block)
    }

    override fun toRequest(): MessageEditRequest = MessageEditRequest(content, embed?.toRequest())
}