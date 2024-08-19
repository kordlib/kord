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
public class CategoryCreateBuilder(
    public var name: String,
) : PermissionOverwritesCreateBuilder, AuditRequestBuilder<GuildChannelCreateRequest> {
    override var reason: String? = null

    private var _position: OptionalInt = OptionalInt.Missing
    public var position: Int? by ::_position.delegate()

    private var _nsfw: OptionalBoolean = OptionalBoolean.Missing
    public var nsfw: Boolean? by ::_nsfw.delegate()

    override var permissionOverwrites: MutableSet<Overwrite> = mutableSetOf()

    override fun toRequest(): GuildChannelCreateRequest = GuildChannelCreateRequest(
        name = name,
        position = _position,
        nsfw = _nsfw,
        permissionOverwrite = Optional.missingOnEmpty(permissionOverwrites),
        type = ChannelType.GuildCategory
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CategoryCreateBuilder

        if (name != other.name) return false
        if (reason != other.reason) return false
        if (position != other.position) return false
        if (nsfw != other.nsfw) return false
        if (permissionOverwrites != other.permissionOverwrites) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (position ?: 0)
        result = 31 * result + (nsfw?.hashCode() ?: 0)
        result = 31 * result + permissionOverwrites.hashCode()
        return result
    }

}
