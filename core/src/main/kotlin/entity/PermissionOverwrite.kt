package dev.kord.core.entity

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.PermissionOverwriteData
import dev.kord.rest.json.request.ChannelPermissionEditRequest

open class PermissionOverwrite constructor(
    val data: PermissionOverwriteData,
) {
    val allowed: Permissions get() = data.allowed
    val denied: Permissions get() = data.denied
    val target: Snowflake get() = data.id
    val type: OverwriteType get() = data.type

    internal fun asRequest() = ChannelPermissionEditRequest(allowed, denied, type)

    internal fun toOverwrite() = Overwrite(id = target, type = type, allow = allowed, deny = denied)

    override fun hashCode(): Int = target.hashCode()
    override fun equals(other: Any?): Boolean {
        val otherOverwrite = other as? PermissionOverwrite ?: return false

        return target == otherOverwrite.target
    }

    companion object {
        fun forEveryone(guildId: Snowflake, allowed: Permissions = Permissions(), denied: Permissions = Permissions()) =
            PermissionOverwrite(PermissionOverwriteData(guildId, OverwriteType.Role, allowed, denied))

        fun forMember(memberId: Snowflake, allowed: Permissions = Permissions(), denied: Permissions = Permissions()) =
            PermissionOverwrite(PermissionOverwriteData(memberId, OverwriteType.Member, allowed, denied))

        fun forRole(roleId: Snowflake, allowed: Permissions = Permissions(), denied: Permissions = Permissions()) =
            PermissionOverwrite(PermissionOverwriteData(roleId, OverwriteType.Role, allowed, denied))
    }

    override fun toString(): String {
        return "PermissionOverwrite(target=$target, type=$type, allowed=$allowed, denied=$denied)"
    }

}