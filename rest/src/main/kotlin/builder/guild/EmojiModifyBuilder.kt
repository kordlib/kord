package dev.kord.rest.builder.guild

import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.EmojiModifyRequest

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