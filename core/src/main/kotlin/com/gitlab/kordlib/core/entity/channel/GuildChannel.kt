package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.common.entity.OverwriteType
import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.orEmpty
import com.gitlab.kordlib.common.entity.optional.value
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.cache.data.PermissionOverwriteData
import com.gitlab.kordlib.core.entity.PermissionOverwrite
import com.gitlab.kordlib.core.entity.PermissionOverwriteEntity
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

/**
 * An instance of a Discord channel associated to a [guild].
 */
interface GuildChannel : Channel, GuildChannelBehavior {

    override val guildId: Snowflake
        get() = data.guildId.value!!

    /**
     * The name of this channel.
     */
    val name: String get() = data.name.value!!

    /**
     * The raw position of this channel in the guild as displayed by Discord.
     */
    val rawPosition: Int get() = data.position.value!!

    /**
     * The permission overwrites for this channel.
     */
    val permissionOverwrites: Set<PermissionOverwriteEntity>
        get() = data.permissionOverwrites.orEmpty().asSequence()
                .map { PermissionOverwriteData(it.id, it.type, it.allow, it.deny) }
                .map { PermissionOverwriteEntity(guildId, id, it, kord) }
                .toSet()

    /**
     * Calculates the effective permissions of the [memberId] in this channel, applying the overwrite for the member
     * and their roles on top of the base permissions.
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws IllegalArgumentException if the [memberId] is not part of this guild.
     */
    suspend fun getEffectivePermissions(memberId: Snowflake): Permissions {
        val member = supplier.getMemberOrNull(guildId, memberId)
        require(member != null) {
            "member ${memberId.asString} is not in guild ${guildId.asString}"
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
                -it.denied
            }
        }

    }

    /**
     * Gets the permission overwrite for the [memberId] in this channel, if present.
     */
    fun getPermissionOverwritesForMember(memberId: Snowflake): PermissionOverwriteEntity? =
            getPermissionOverwritesForType(memberId, OverwriteType.Member)

    /**
     * Gets the permission overwrite for the [roleId] in this channel, if present.
     */
    fun getPermissionOverwritesForRole(roleId: Snowflake): PermissionOverwriteEntity? =
            getPermissionOverwritesForType(roleId, OverwriteType.Role)

    private fun getPermissionOverwritesForType(id: Snowflake, type: OverwriteType): PermissionOverwriteEntity? =
            permissionOverwrites.firstOrNull { it.target == id && it.type == type }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildChannel

}