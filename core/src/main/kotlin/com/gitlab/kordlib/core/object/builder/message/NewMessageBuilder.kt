package com.gitlab.kordlib.core.`object`.builder.message

import com.gitlab.kordlib.rest.json.request.MessageCreateRequest

//TODO files/multipart

class NewMessageBuilder(
        var content: String? = null,
        var nonce: String? = null,
        var tts: Boolean? = null,
        var embed: Embed? = null
) {

    inline fun embed(block: Embed.() -> Unit) {
        embed = (embed ?: Embed()).apply(block)
    }

    fun toRequest(): MessageCreateRequest = MessageCreateRequest(content, nonce, tts, embed?.toRequest())

}