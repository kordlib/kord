package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildChannelCreateRequest

@KordDsl
class CategoryCreateBuilder(var name: String) : PermissionOverritesBuilder, AuditRequestBuilder<GuildChannelCreateRequest> {
    override var reason: String? = null

    private var _position: OptionalInt = OptionalInt.Missing
    var position: Int? by ::_position.delegate()

    private var _nsfw: OptionalBoolean = OptionalBoolean.Missing
    var nsfw: Boolean? by ::_nsfw.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>> = Optional.Missing()
    override var permissionOverwrites by ::_permissionOverwrites.delegate()

    override fun toRequest(): GuildChannelCreateRequest = GuildChannelCreateRequest(
        name = name,
        position = _position,
        nsfw = _nsfw,
        permissionOverwrite = _permissionOverwrites,
        type = ChannelType.GuildCategory
    )
}
