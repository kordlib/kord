package com.gitlab.kordlib.core.builder.guild

import com.gitlab.kordlib.core.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.builder.KordBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.EmojiModifyRequest

@KordBuilder
class EmojiModifyBuilder : AuditRequestBuilder<EmojiModifyRequest> {
    override var reason: String? = null
    var name: String? = null
    val roles: MutableSet<Snowflake> = mutableSetOf()

    override fun toRequest(): EmojiModifyRequest = EmojiModifyRequest(
            name = name,
            roles = roles.map { it.value }
    )
}