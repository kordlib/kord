package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.annotation.KordUnstableApi

@KordDsl
class PermissionOverwriteBuilder(private val type: String, private val id: Snowflake) {
    var allowed: Permissions = Permissions()
    var denied: Permissions = Permissions()

    @OptIn(KordUnstableApi::class)
    fun toOverwrite(): Overwrite = Overwrite(id = id.value, allow = allowed.code, deny = denied.code, type = type)
}
