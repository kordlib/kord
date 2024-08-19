package dev.kord.rest.builder.guild

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.EmojiCreateRequest

@KordDsl
public class EmojiCreateBuilder(
    public var name: String,
    public var image: Image,
) : AuditRequestBuilder<EmojiCreateRequest> {
    override var reason: String? = null

    public var roles: MutableSet<Snowflake> = mutableSetOf()

    override fun toRequest(): EmojiCreateRequest = EmojiCreateRequest(
        name = name,
        image = image.dataUri,
        roles = roles
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EmojiCreateBuilder

        if (name != other.name) return false
        if (image != other.image) return false
        if (reason != other.reason) return false
        if (roles != other.roles) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + roles.hashCode()
        return result
    }

}
