package com.gitlab.kordlib.rest.builder.guild

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.rest.json.request.EmojiModifyRequest

@KordDsl
class EmojiModifyBuilder : AuditRequestBuilder<EmojiModifyRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    var roles: MutableSet<Snowflake> = mutableSetOf()

    override fun toRequest(): EmojiModifyRequest = EmojiModifyRequest(
            name = _name,
            roles = Optional.missingOnEmpty(roles)
    )
}