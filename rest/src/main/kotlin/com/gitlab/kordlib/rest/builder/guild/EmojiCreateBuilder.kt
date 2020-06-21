package com.gitlab.kordlib.rest.builder.guild

import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.json.request.EmojiCreateRequest
import com.gitlab.kordlib.rest.json.request.EmojiModifyRequest

@KordDsl
class EmojiCreateBuilder : AuditRequestBuilder<EmojiCreateRequest> {
    override var reason: String? = null
    lateinit var name: String
    lateinit var image: Image
    var roles: Set<Snowflake> = emptySet()

    override fun toRequest(): EmojiCreateRequest = EmojiCreateRequest(
            name = name,
            image = image.dataUri,
            roles = roles.map { it.value }
    )
}
