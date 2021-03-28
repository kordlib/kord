package dev.kord.rest.builder.guild

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.EmojiCreateRequest

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
