package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.OverwriteType

@KordDsl
class PermissionOverwriteBuilder(private val type: OverwriteType, private val id: Snowflake) {
    var allowed: Permissions = Permissions()
    var denied: Permissions = Permissions()

    fun toOverwrite(): Overwrite = Overwrite(id = id, allow = allowed, deny = denied, type = type)
}
