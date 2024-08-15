package dev.kord.rest.builder.member

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildMemberAddRequest

@KordDsl
public class MemberAddBuilder(public var token: String) : RequestBuilder<GuildMemberAddRequest> {

    private var _nickname: Optional<String> = Optional.Missing()
    public var nickname: String? by ::_nickname.delegate()

    public var roles: MutableSet<Snowflake> = mutableSetOf()

    private var _muted: OptionalBoolean = OptionalBoolean.Missing
    public var muted: Boolean? by ::_muted.delegate()

    private var _deafened: OptionalBoolean = OptionalBoolean.Missing
    public var deafened: Boolean? by ::_deafened.delegate()

    override fun toRequest(): GuildMemberAddRequest = GuildMemberAddRequest(
        token, _nickname, Optional.missingOnEmpty(roles), mute = _muted, deaf = _deafened
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MemberAddBuilder

        if (token != other.token) return false
        if (nickname != other.nickname) return false
        if (roles != other.roles) return false
        if (muted != other.muted) return false
        if (deafened != other.deafened) return false

        return true
    }

    override fun hashCode(): Int {
        var result = token.hashCode()
        result = 31 * result + (nickname?.hashCode() ?: 0)
        result = 31 * result + roles.hashCode()
        result = 31 * result + (muted?.hashCode() ?: 0)
        result = 31 * result + (deafened?.hashCode() ?: 0)
        return result
    }

}
