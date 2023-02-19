package dev.kord.core.cache.data

import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.ApplicationCommandPermissionType
import dev.kord.common.entity.DiscordGuildApplicationCommandPermission
import dev.kord.common.entity.Snowflake
import kotlin.DeprecationLevel.HIDDEN


public data class GuildApplicationCommandPermissionData(
    val id: Snowflake,
    val type: ApplicationCommandPermissionType,
    val permission: Boolean
) {
    @Suppress("DEPRECATION_ERROR")
    @Deprecated(
        "'DiscordGuildApplicationCommandPermission.Type' is replaced by 'ApplicationCommandPermissionType'",
        level = HIDDEN,
    )
    public constructor(id: Snowflake, type: DiscordGuildApplicationCommandPermission.Type, permission: Boolean) : this(
        id,
        type.toNewType(),
        permission,
    )

    @Suppress("DEPRECATION_ERROR")
    @Deprecated("Binary compatibility", level = HIDDEN)
    @get:JvmName("getType")
    public val type0: DiscordGuildApplicationCommandPermission.Type get() = type.toDeprecatedType()

    @Suppress("DEPRECATION_ERROR", "FunctionName")
    @Deprecated("Binary compatibility", level = HIDDEN)
    @JvmName("component2")
    public fun _component2(): DiscordGuildApplicationCommandPermission.Type = type.toDeprecatedType()

    @Suppress("DEPRECATION_ERROR")
    @Deprecated(
        "'DiscordGuildApplicationCommandPermission.Type' is replaced by 'ApplicationCommandPermissionType'",
        level = HIDDEN,
    )
    public fun copy(
        id: Snowflake = this.id,
        type: DiscordGuildApplicationCommandPermission.Type = this.type.toDeprecatedType(),
        permission: Boolean = this.permission,
    ): GuildApplicationCommandPermissionData = GuildApplicationCommandPermissionData(id, type.toNewType(), permission)


    public companion object {
        public fun from(permission: DiscordGuildApplicationCommandPermission): GuildApplicationCommandPermissionData =
            with(permission) {
                GuildApplicationCommandPermissionData(id, type, this.permission)
            }


        // functions for migration purposes, remove when bumping deprecations
        @Suppress("DEPRECATION_ERROR", "INVISIBLE_MEMBER")
        private fun ApplicationCommandPermissionType.toDeprecatedType() = when (this) {
            ApplicationCommandPermissionType.Role -> DiscordGuildApplicationCommandPermission.Type.Role
            ApplicationCommandPermissionType.User -> DiscordGuildApplicationCommandPermission.Type.User
            ApplicationCommandPermissionType.Channel -> DiscordGuildApplicationCommandPermission.Type.Channel
            is ApplicationCommandPermissionType.Unknown -> DiscordGuildApplicationCommandPermission.Type.Unknown(value, null)
        }

        @OptIn(KordUnsafe::class)
        @Suppress("DEPRECATION_ERROR")
        private fun DiscordGuildApplicationCommandPermission.Type.toNewType() = when (this) {
            DiscordGuildApplicationCommandPermission.Type.Role -> ApplicationCommandPermissionType.Role
            DiscordGuildApplicationCommandPermission.Type.User -> ApplicationCommandPermissionType.User
            DiscordGuildApplicationCommandPermission.Type.Channel -> ApplicationCommandPermissionType.Channel
            is DiscordGuildApplicationCommandPermission.Type.Unknown -> ApplicationCommandPermissionType.Unknown(value)
        }
    }
}
