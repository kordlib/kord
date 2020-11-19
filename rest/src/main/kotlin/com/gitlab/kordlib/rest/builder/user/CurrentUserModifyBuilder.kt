package com.gitlab.kordlib.rest.builder.user

import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.delegate.delegate
import com.gitlab.kordlib.common.entity.optional.map
import com.gitlab.kordlib.common.entity.optional.mapNullable
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.CurrentUserModifyRequest

@KordDsl
class CurrentUserModifyBuilder : RequestBuilder<CurrentUserModifyRequest> {

    private var _username: Optional<String> = Optional.Missing()
    var username: String? by ::_username.delegate()

    private var _avatar: Optional<Image?> = Optional.Missing()
    var avatar: Image? by ::_avatar.delegate()

    override fun toRequest(): CurrentUserModifyRequest = CurrentUserModifyRequest(
            _username, _avatar.mapNullable { it?.dataUri }
    )

}