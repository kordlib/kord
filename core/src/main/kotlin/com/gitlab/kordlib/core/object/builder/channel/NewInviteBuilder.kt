package com.gitlab.kordlib.core.`object`.builder.channel

import com.gitlab.kordlib.rest.json.request.InviteCreateRequest

class NewInviteBuilder (
        /**
         * The duration of invite in seconds before expiry, or 0 for never. 86400 (24 hours) by default.
         */
        var age: Int? = null,
        /**
         * The maximum number of uses, or 0 for unlimited. 0 by default.
         */
        var uses: Int? = null,
        /**
         * 	Whether this invite only grants temporary membership. False by default.
         */
        var temporary: Boolean? = null,

        /**
         * Whether to reuse a similar invite (useful for creating many unique one time use invites). False by default.
         */
        var unique: Boolean? = null
) {
    fun toRequest(): InviteCreateRequest = InviteCreateRequest(
            temporary = temporary ?: false,
            age = age ?: 0,
            unique = unique ?: false,
            uses = uses ?: 0
    )
}


