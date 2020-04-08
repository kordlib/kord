package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.InviteCreateRequest
import com.gitlab.kordlib.rest.json.response.TargetUserType

@KordDsl
class InviteCreateBuilder : RequestBuilder<InviteCreateRequest> {
    /**
     * The duration of invite in seconds before expiry, or 0 for never. 86400 (24 hours) by default.
     */
    var age: Int = 86400

    /**
     * The maximum number of uses, or 0 for unlimited. 0 by default.
     */
    var uses: Int = 0

    /**
     * 	Whether this invite only grants temporary membership. False by default.
     */
    var temporary: Boolean = false

    /**
     * Whether to reuse a similar invite (useful for creating many unique one time use invites). False by default.
     */
    var unique: Boolean = false

    /**
     * The target user id for this invite.
     */
    var targetUser: Snowflake? = null

    /**
     * The audit log reason for creating this invite.
     */
    var reason: String? = null

    override fun toRequest(): InviteCreateRequest = InviteCreateRequest(
            temporary = temporary,
            age = age,
            unique = unique,
            uses = uses,
            targetUser = targetUser?.value,
            targetUserType = targetUser?.let { TargetUserType.STREAM }
    )
}



