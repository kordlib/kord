package com.gitlab.kordlib.core.`object`.builder.message

import com.gitlab.kordlib.rest.json.request.MessageCreateRequest

//TODO files/multipart

class NewMessageBuilder(
        var content: String? = null,
        var nonce: String? = null,
        var tts: Boolean? = null,
        var embed: EmbedBuilder? = null
) {

    inline fun embed(block: EmbedBuilder.() -> Unit) {
        embed = (embed ?: EmbedBuilder()).apply(block)
    }

    fun toRequest(): MessageCreateRequest = MessageCreateRequest(content, nonce, tts, embed?.toRequest())

}