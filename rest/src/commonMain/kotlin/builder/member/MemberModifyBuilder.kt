package dev.kord.rest.builder.member

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildMemberModifyRequest
import kotlinx.datetime.Instant

@KordDsl
public class MemberModifyBuilder : AuditRequestBuilder<GuildMemberModifyRequest> {
    override var reason: String? = null

    private var _voiceChannelId: OptionalSnowflake? = OptionalSnowflake.Missing
    public var voiceChannelId: Snowflake? by ::_voiceChannelId.delegate()

    private var _muted: OptionalBoolean? = OptionalBoolean.Missing
    public var muted: Boolean? by ::_muted.delegate()

    private var _deafened: OptionalBoolean? = OptionalBoolean.Missing
    public var deafened: Boolean? by ::_deafened.delegate()

    private var _nickname: Optional<String?> = Optional.Missing()
    public var nickname: String? by ::_nickname.delegate()

    private var _communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
    public var communicationDisabledUntil: Instant? by ::_communicationDisabledUntil.delegate()

    private var _roles: Optional<MutableSet<Snowflake>?> = Optional.Missing()
    public var roles: MutableSet<Snowflake>? by ::_roles.delegate()

    override fun toRequest(): GuildMemberModifyRequest = GuildMemberModifyRequest(
        nick = _nickname,
        channelId = _voiceChannelId,
        mute = _muted,
        deaf = _deafened,
        roles = _roles,
        communicationDisabledUntil = _communicationDisabledUntil
    )
}
