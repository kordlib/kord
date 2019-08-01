package com.gitlab.kordlib.core.`object`

import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.EditChannelPermissionRequest

class PermissionOverwrite private constructor(
        val allowed: Permissions,
        val denied: Permissions,
        val target: Snowflake,
        val type: String
) {

    internal fun asRequest() = EditChannelPermissionRequest(allowed, denied, type)

    companion object {
        fun forMember(member: Snowflake, allowed: Permissions = Permissions(), denied: Permissions = Permissions()) =
                PermissionOverwrite(allowed, denied, member, "member")

        fun forRole(role: Snowflake, allowed: Permissions = Permissions(), denied: Permissions = Permissions()) =
                PermissionOverwrite(allowed, denied, role, "role")
    }

}