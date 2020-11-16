package com.gitlab.kordlib.rest.builder.member

import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.rest.json.request.GuildMemberModifyRequest

@KordDsl
class MemberModifyBuilder : AuditRequestBuilder<GuildMemberModifyRequest> {
    override var reason: String? = null

    private var _voiceChannelId: OptionalSnowflake = OptionalSnowflake.Missing
    var voiceChannelId: Snowflake? by ::_voiceChannelId.delegate()

    private var _muted: OptionalBoolean = OptionalBoolean.Missing
    var muted: Boolean? by ::_muted.delegate()

    private var _deafened: OptionalBoolean = OptionalBoolean.Missing
    var deafened: Boolean? by ::_deafened.delegate()

    private var _nickname: Optional<String> = Optional.Missing()
    var nickname: String? by ::_nickname.delegate()

    var roles: MutableSet<Snowflake> = mutableSetOf()

    override fun toRequest(): GuildMemberModifyRequest = GuildMemberModifyRequest(
            nick = _nickname,
            channelId = _voiceChannelId,
            mute = _muted,
            deaf = _deafened,
            roles = Optional.missingOnEmpty(roles)
    )
}