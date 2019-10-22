package com.gitlab.kordlib.core.builder.channel

import com.gitlab.kordlib.core.builder.KordDsl
import com.gitlab.kordlib.core.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.InviteCreateRequest

@KordDsl
class InviteCreateBuilder: RequestBuilder<InviteCreateRequest> {
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

    override fun toRequest(): InviteCreateRequest = InviteCreateRequest(
            temporary = temporary,
            age = age,
            unique = unique,
            uses = uses
    )
}


