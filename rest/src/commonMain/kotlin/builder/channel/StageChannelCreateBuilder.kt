package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildChannelCreateRequest
import kotlin.time.Duration

@KordDsl
public class StageChannelCreateBuilder(public var name: String) :
    PermissionOverwritesCreateBuilder,
    AuditRequestBuilder<GuildChannelCreateRequest> {
    override var reason: String? = null

    private var _bitrate: OptionalInt = OptionalInt.Missing
    public var bitrate: Int? by ::_bitrate.delegate()

    private var _userLimit: OptionalInt = OptionalInt.Missing
    public var userLimit: Int? by ::_userLimit.delegate()

    private var _rateLimitPerUser: Optional<Duration> = Optional.Missing()
    public var rateLimitPerUser: Duration? by ::_rateLimitPerUser.delegate()

    private var _position: OptionalInt = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    override var permissionOverwrites: MutableSet<Overwrite> = mutableSetOf()

    private var _parentId: OptionalSnowflake = OptionalSnowflake.Missing
    public var parentId: Snowflake? by ::_parentId.delegate()

    private var _nsfw: OptionalBoolean = OptionalBoolean.Missing
    public var nsfw: Boolean? by ::_nsfw.delegate()

    override fun toRequest(): GuildChannelCreateRequest = GuildChannelCreateRequest(
        name = name,
        type = ChannelType.GuildStageVoice,
        bitrate = _bitrate,
        userLimit = _userLimit,
        rateLimitPerUser = _rateLimitPerUser,
        position = _position,
        permissionOverwrite = Optional.missingOnEmpty(permissionOverwrites),
        parentId = _parentId,
        nsfw = _nsfw
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StageChannelCreateBuilder

        if (name != other.name) return false
        if (reason != other.reason) return false
        if (bitrate != other.bitrate) return false
        if (userLimit != other.userLimit) return false
        if (rateLimitPerUser != other.rateLimitPerUser) return false
        if (position != other.position) return false
        if (permissionOverwrites != other.permissionOverwrites) return false
        if (parentId != other.parentId) return false
        if (nsfw != other.nsfw) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (bitrate ?: 0)
        result = 31 * result + (userLimit ?: 0)
        result = 31 * result + (rateLimitPerUser?.hashCode() ?: 0)
        result = 31 * result + (position ?: 0)
        result = 31 * result + permissionOverwrites.hashCode()
        result = 31 * result + (parentId?.hashCode() ?: 0)
        result = 31 * result + (nsfw?.hashCode() ?: 0)
        return result
    }

}
