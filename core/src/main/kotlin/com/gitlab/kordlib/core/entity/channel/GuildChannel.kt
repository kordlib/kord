package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.cache.data.PermissionOverwriteData
import com.gitlab.kordlib.core.entity.PermissionOverwrite
import com.gitlab.kordlib.core.entity.PermissionOverwriteEntity

/**
 * An instance of a Discord channel associated to a [guild].
 */
interface GuildChannel : Channel, GuildChannelBehavior {

    override val guildId: Snowflake
        get() = Snowflake(data.guildId!!)

    /**
     * The name of this channel.
     */
    val name get() = data.name!!

    /**
     * The raw position of this channel in the guild as displayed by Discord.
     */
    val rawPosition get() = data.position!!

    /**
     * The permission overwrites for this channel.
     */
    val permissionOverwrites: Set<PermissionOverwriteEntity>
        get() = data.permissionOverwrites.asSequence()
                .map { PermissionOverwriteData(it.id, it.type, it.allowed, it.denied) }
                .map { PermissionOverwriteEntity(guildId, id, it, kord) }
                .toSet()

    /**
     * Calculates the effective permissions of the [memberId] in this channel, applying the overwrite for the member
     * and their roles on top of the base permissions.
     */
    suspend fun getEffectivePermissions(memberId: Snowflake): Permissions {
        val member = strategy.supply(kord).getMemberOrNull(guildId, memberId)
        require(member != null) {
            "member ${memberId.value} is not in guild ${guildId.value}"
        }

        val base = member.getPermissions()

        if (Permission.Administrator in base) return Permissions { +Permission.All }

        val everyoneOverwrite = getPermissionOverwritesForRole(guildId)
        val roleOverwrites = member.roleIds.mapNotNull { getPermissionOverwritesForRole(it) }
        val memberOverwrite = getPermissionOverwritesForMember(memberId)

        return Permissions {
            +base
            everyoneOverwrite?.let {
                +it.allowed
                -it.denied
            }
            roleOverwrites.map {
                +it.allowed
                -it.denied
            }
            memberOverwrite?.let {
                +it.allowed
                +it.denied
            }
        }

    }

    /**
     * Gets the permission overwrite for the [memberId] in this channel, if present.
     */
    fun getPermissionOverwritesForMember(memberId: Snowflake): PermissionOverwriteEntity? =
            getPermissionOverwritesForType(memberId, PermissionOverwrite.Type.Member)

    /**
     * Gets the permission overwrite for the [roleId] in this channel, if present.
     */
    fun getPermissionOverwritesForRole(roleId: Snowflake): PermissionOverwriteEntity? =
            getPermissionOverwritesForType(roleId, PermissionOverwrite.Type.Role)

    private fun getPermissionOverwritesForType(id: Snowflake, type: PermissionOverwrite.Type): PermissionOverwriteEntity? =
            permissionOverwrites.firstOrNull { it.target == id && it.type == type }
}