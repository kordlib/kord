package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.TargetUserType
import com.gitlab.kordlib.common.entity.optional.*
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.InviteCreateRequest

@KordDsl
class InviteCreateBuilder : AuditRequestBuilder<InviteCreateRequest> {

    private var _age: OptionalInt = OptionalInt.Missing

    /**
     * The duration of invite in seconds before expiry, or 0 for never. 86400 (24 hours) by default.
     */
    var age: Int? by ::_age.delegate()

    private var _uses: OptionalInt = OptionalInt.Missing
    /**
     * The maximum number of uses, or 0 for unlimited. 0 by default.
     */
    var uses: Int? by ::_uses.delegate()

    private var _temporary: OptionalBoolean = OptionalBoolean.Missing
    /**
     * 	Whether this invite only grants temporary membership. False by default.
     */
    var temporary: Boolean? by ::_temporary.delegate()

    private var _unique: OptionalBoolean = OptionalBoolean.Missing
    /**
     * Whether to reuse a similar invite (useful for creating many unique one time use invites). False by default.
     */
    var unique: Boolean? by ::_unique.delegate()

    private var _targetUser: OptionalSnowflake = OptionalSnowflake.Missing
    /**
     * The target user id for this invite.
     */
    var targetUser: Snowflake? by ::_targetUser.delegate()

    private var _reason: Optional<String> = Optional.Missing()
    override var reason: String? by ::_reason.delegate()

    override fun toRequest(): InviteCreateRequest = InviteCreateRequest(
            temporary = _temporary,
            age = _age,
            unique = _unique,
            uses = _uses,
            targetUser = _targetUser,
            targetUserType = _targetUser.map { TargetUserType.Stream }
    )
}



