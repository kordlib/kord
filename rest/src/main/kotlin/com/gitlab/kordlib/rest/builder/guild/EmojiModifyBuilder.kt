package com.gitlab.kordlib.rest.builder.guild

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.builder.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.EmojiModifyRequest

@KordDsl
class EmojiModifyBuilder : AuditRequestBuilder<EmojiModifyRequest> {
    override var reason: String? = null
    var name: String? = null
    var roles: Set<Snowflake>? = null

    override fun toRequest(): EmojiModifyRequest = EmojiModifyRequest(
            name = name,
            roles = roles?.map { it.value }
    )
}