package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildChannelCreateRequest

@KordDsl
public class VoiceChannelCreateBuilder(public var name: String) :
    PermissionOverwritesCreateBuilder,
    AuditRequestBuilder<GuildChannelCreateRequest> {
    override var reason: String? = null

    private var _bitrate: OptionalInt = OptionalInt.Missing
    public var bitrate: Int? by ::_bitrate.delegate()

    private var _userLimit: OptionalInt = OptionalInt.Missing
    public var userLimit: Int? by ::_userLimit.delegate()

    private var _parentId: OptionalSnowflake = OptionalSnowflake.Missing
    public var parentId: Snowflake? by ::_parentId.delegate()

    private var _position: OptionalInt = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    override var permissionOverwrites: MutableSet<Overwrite> = mutableSetOf()

    override fun toRequest(): GuildChannelCreateRequest = GuildChannelCreateRequest(
        name = name,
        bitrate = _bitrate,
        userLimit = _userLimit,
        parentId = _parentId,
        position = _position,
        permissionOverwrite = Optional.missingOnEmpty(permissionOverwrites),
        type = ChannelType.GuildVoice
    )
}
