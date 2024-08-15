package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake

@KordDsl
public class PermissionOverwriteBuilder(private val type: OverwriteType, private val id: Snowflake) {
    public var allowed: Permissions = Permissions()
    public var denied: Permissions = Permissions()

    public fun toOverwrite(): Overwrite = Overwrite(id = id, allow = allowed, deny = denied, type = type)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PermissionOverwriteBuilder

        if (type != other.type) return false
        if (id != other.id) return false
        if (allowed != other.allowed) return false
        if (denied != other.denied) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + allowed.hashCode()
        result = 31 * result + denied.hashCode()
        return result
    }

}
