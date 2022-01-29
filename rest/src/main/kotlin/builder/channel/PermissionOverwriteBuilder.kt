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
}
