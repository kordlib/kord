package dev.kord.rest.builder.member

import dev.kord.common.entity.Snowflake
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildMemberAddRequest

@KordDsl
class MemberAddBuilder(var token: String) : RequestBuilder<GuildMemberAddRequest> {

    private var _nickname: Optional<String> = Optional.Missing()
    var nickname: String? by ::_nickname.delegate()

    var roles: MutableSet<Snowflake> = mutableSetOf()

    private var _muted: OptionalBoolean = OptionalBoolean.Missing
    var muted: Boolean? by ::_muted.delegate()

    private var _deafened: OptionalBoolean = OptionalBoolean.Missing
    var deafened: Boolean? by ::_deafened.delegate()

    override fun toRequest(): GuildMemberAddRequest = GuildMemberAddRequest(
            token, _nickname, Optional.missingOnEmpty(roles), mute = _muted, deaf = _deafened
    )

}
