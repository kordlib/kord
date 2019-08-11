package com.gitlab.kordlib.core.`object`.builder.role

import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.rest.json.request.GuildRoleModifyRequest
import java.awt.Color

class UpdateRoleBuilder (
        var name: String? = null,
        var color: Color? = null,
        var hoist: Boolean? = null,
        var mentionable: Boolean? = null,
        var permissions: Permissions? = null
) {
    fun toRequest(): GuildRoleModifyRequest = GuildRoleModifyRequest(
            name = name,
            color = color?.rgb,
            separate = hoist,
            mentionable = mentionable,
            permissions = permissions
    )
}