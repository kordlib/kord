package com.gitlab.kordlib.core.`object`.builder.message

import com.gitlab.kordlib.rest.json.request.MessageEditRequest

class EditMessageBuilder (
        var content: String? = null,
        var embed: EmbedBuilder? = null
) {

    fun embed(block: EmbedBuilder.() -> Unit) {
        embed = (embed ?: EmbedBuilder()).also(block)
    }

    fun toRequest(): MessageEditRequest = MessageEditRequest(content, embed?.toRequest())

}