package com.gitlab.kordlib.core.`object`.builder.message

import com.gitlab.kordlib.rest.json.request.MessageEditRequest

class EditMessageBuilder (        var content: String? = null,
        var embed: Embed? = null
) {

    fun embed(block: Embed.() -> Unit) {
        embed = (embed ?: Embed()).also(block)
    }

    fun toRequest(): MessageEditRequest = MessageEditRequest(content, embed?.toRequest())

}