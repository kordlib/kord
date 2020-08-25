package com.gitlab.kordlib.rest.builder.message

import com.gitlab.kordlib.common.entity.Flags
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.MessageEditPatchRequest

@KordDsl
class MessageModifyBuilder : RequestBuilder<@OptIn(KordUnstableApi::class) MessageEditPatchRequest> {
    var content: String? = null
    var embed: EmbedBuilder? = null
    var flags: Flags? = null
    var allowedMentions: AllowedMentionsBuilder? = null

    inline fun embed(block: EmbedBuilder.() -> Unit) {
        embed = (embed ?: EmbedBuilder()).also(block)
    }

    /**
     * Configures the mentions that should trigger a ping. Not calling this function will result in the default behavior
     * (ping everything), calling this function but not configuring it before the request is build will result in all
     * pings being ignored.
     */
    inline fun allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
        allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
    }


    @OptIn(KordUnstableApi::class)
    override fun toRequest(): MessageEditPatchRequest = MessageEditPatchRequest(content, embed?.toRequest(), flags, allowedMentions?.build())
}
