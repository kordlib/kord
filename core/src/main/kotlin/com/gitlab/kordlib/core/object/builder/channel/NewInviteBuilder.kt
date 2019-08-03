package com.gitlab.kordlib.core.`object`.builder.channel

import com.gitlab.kordlib.rest.json.request.InviteCreateRequest

class NewInviteBuilder (
        var age: Int? = null,
        var uses: Int? = null,
        var temporary: Boolean? = null,
        var unique: Boolean? = null
) {
    internal fun toRequest(): InviteCreateRequest = InviteCreateRequest(
            temporary = temporary ?: false,
            age = age ?: 0,
            unique = unique ?: false,
            uses = uses ?: 0
    )
}


