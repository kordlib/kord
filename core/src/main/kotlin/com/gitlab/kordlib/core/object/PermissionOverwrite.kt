package com.gitlab.kordlib.core.`object`

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.core.`object`.data.PermissionOverwriteData
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.ChannelPermissionEditRequest

open class PermissionOverwrite constructor(
        val data: PermissionOverwriteData
) {
    val allowed: Permissions get() = Permissions(data.allowed)
    val denied: Permissions get() = Permissions(data.denied)
    val target: Snowflake get() = Snowflake(data.id)
    val type: Type get() = Type.from(data.type)

    internal fun asRequest() = ChannelPermissionEditRequest(allowed, denied, type.value)

    internal fun toOverwrite() = Overwrite(id = target.value, type = type.value, allow = allowed.code, deny = denied.code)

    override fun hashCode(): Int = target.hashCode()
    override fun equals(other: Any?): Boolean {
        val otherOverwrite = other as? PermissionOverwrite ?: return false

        return target == otherOverwrite.target
    }

    companion object {
        fun forEveryone(guildId: Snowflake, allowed: Permissions = Permissions(), denied: Permissions = Permissions()) =
                PermissionOverwrite(PermissionOverwriteData(guildId.value, Type.Role.value, allowed.code, denied.code))

        fun forMember(memberId: Snowflake, allowed: Permissions = Permissions(), denied: Permissions = Permissions()) =
                PermissionOverwrite(PermissionOverwriteData(memberId.value, Type.Member.value, allowed.code, denied.code))

        fun forRole(roleId: Snowflake, allowed: Permissions = Permissions(), denied: Permissions = Permissions()) =
                PermissionOverwrite(PermissionOverwriteData(roleId.value, Type.Role.value, allowed.code, denied.code))
    }

    sealed class Type(val value: String) {
        object Role : Type("role")

        object Member : Type("member")

        companion object {
            fun from(type: String) = when (type) {
                "role" -> Role
                "member" -> Member
                else -> throw IllegalArgumentException("unknown PermissionOverwrite.Type: $type")
            }
        }
    }
}