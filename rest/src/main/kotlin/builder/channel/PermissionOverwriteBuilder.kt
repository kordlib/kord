package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake

@KordDsl
class PermissionOverwriteBuilder(private val type: OverwriteType, private val id: Snowflake) {
    var allowed: Permissions = Permissions()
    var denied: Permissions = Permissions()

    fun toOverwrite(): Overwrite = Overwrite(id = id, allow = allowed, deny = denied, type = type)
}
