package com.gitlab.kordlib.rest.builder.member

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.GuildMemberAddRequest

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
