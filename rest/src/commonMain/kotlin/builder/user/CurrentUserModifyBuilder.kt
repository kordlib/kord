package dev.kord.rest.builder.user

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.CurrentUserModifyRequest

@KordDsl
public class CurrentUserModifyBuilder : RequestBuilder<CurrentUserModifyRequest> {

    private var _username: Optional<String> = Optional.Missing()
    public var username: String? by ::_username.delegate()

    private var _avatar: Optional<Image?> = Optional.Missing()
    public var avatar: Image? by ::_avatar.delegate()

    override fun toRequest(): CurrentUserModifyRequest = CurrentUserModifyRequest(
        _username, _avatar.map { it.dataUri }
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CurrentUserModifyBuilder

        if (username != other.username) return false
        if (avatar != other.avatar) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username?.hashCode() ?: 0
        result = 31 * result + (avatar?.hashCode() ?: 0)
        return result
    }

}
