package com.gitlab.kordlib.rest.builder.guild

import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.json.request.EmojiCreateRequest

@KordDsl
class EmojiCreateBuilder(var name: String, var image: Image) : AuditRequestBuilder<EmojiCreateRequest> {
    override var reason: String? = null

    var roles: MutableSet<Snowflake> = mutableSetOf()

    override fun toRequest(): EmojiCreateRequest = EmojiCreateRequest(
            name = name,
            image = image.dataUri,
            roles = roles
    )

}
