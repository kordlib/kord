package dev.kord.rest.builder.guild

import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.EmojiModifyRequest

@KordDsl
public class EmojiModifyBuilder : AuditRequestBuilder<EmojiModifyRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _roles: Optional<MutableSet<Snowflake>?> = Optional.Missing()
    public var roles: MutableSet<Snowflake>? by ::_roles.delegate()

    override fun toRequest(): EmojiModifyRequest = EmojiModifyRequest(
        name = _name,
        roles = _roles
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EmojiModifyBuilder

        if (reason != other.reason) return false
        if (name != other.name) return false
        if (roles != other.roles) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reason?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (roles?.hashCode() ?: 0)
        return result
    }

}
