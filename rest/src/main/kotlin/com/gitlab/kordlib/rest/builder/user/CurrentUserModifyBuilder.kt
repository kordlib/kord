package dev.kord.rest.builder.user

import dev.kord.rest.Image
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapNullable
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.CurrentUserModifyRequest

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