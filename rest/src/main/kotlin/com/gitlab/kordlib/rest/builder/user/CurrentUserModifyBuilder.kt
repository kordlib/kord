package com.gitlab.kordlib.rest.builder.user

import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.CurrentUserModifyRequest

@KordDsl
class CurrentUserModifyBuilder : RequestBuilder<@OptIn(KordUnstableApi::class) CurrentUserModifyRequest> {

    var username: String? = null
    var avatar: Image? = null

    @OptIn(KordUnstableApi::class)
    override fun toRequest(): CurrentUserModifyRequest = CurrentUserModifyRequest(username, avatar?.dataUri)

}