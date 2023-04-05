package dev.kord.core.entity

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.PermissionOverwriteData
import dev.kord.rest.json.request.ChannelPermissionEditRequest

public open class PermissionOverwrite(
    public val data: PermissionOverwriteData,
) {
    public val allowed: Permissions get() = data.allowed
    public val denied: Permissions get() = data.denied
    public val target: Snowflake get() = data.id
    public val type: OverwriteType get() = data.type

    internal fun asRequest() = ChannelPermissionEditRequest(allowed, denied, type)

    internal fun toOverwrite() = Overwrite(id = target, type = type, allow = allowed, deny = denied)

    override fun hashCode(): Int = target.hashCode()
    override fun equals(other: Any?): Boolean {
        val otherOverwrite = other as? PermissionOverwrite ?: return false

        return target == otherOverwrite.target
    }

    public companion object {
        public fun forEveryone(
            guildId: Snowflake,
            allowed: Permissions = Permissions(),
            denied: Permissions = Permissions()
        ): PermissionOverwrite =
            PermissionOverwrite(PermissionOverwriteData(guildId, OverwriteType.Role, allowed, denied))

        public fun forMember(
            memberId: Snowflake,
            allowed: Permissions = Permissions(),
            denied: Permissions = Permissions()
        ): PermissionOverwrite =
            PermissionOverwrite(PermissionOverwriteData(memberId, OverwriteType.Member, allowed, denied))

        public fun forRole(
            roleId: Snowflake,
            allowed: Permissions = Permissions(),
            denied: Permissions = Permissions()
        ): PermissionOverwrite =
            PermissionOverwrite(PermissionOverwriteData(roleId, OverwriteType.Role, allowed, denied))
    }

    override fun toString(): String {
        return "PermissionOverwrite(target=$target, type=$type, allowed=$allowed, denied=$denied)"
    }

}
