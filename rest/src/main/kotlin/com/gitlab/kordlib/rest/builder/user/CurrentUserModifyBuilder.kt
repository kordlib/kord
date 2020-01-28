package com.gitlab.kordlib.rest.builder.user

import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.builder.KordDsl
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.CurrentUserModifyRequest

@KordDsl
class CurrentUserModifyBuilder : RequestBuilder<CurrentUserModifyRequest> {

    var username: String? = null
    var avatar: Image? = null

    override fun toRequest(): CurrentUserModifyRequest = CurrentUserModifyRequest(username, avatar?.dataUri)

}