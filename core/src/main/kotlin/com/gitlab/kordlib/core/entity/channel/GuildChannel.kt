package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.cache.data.PermissionOverwriteData
import com.gitlab.kordlib.core.entity.PermissionOverwrite
import com.gitlab.kordlib.core.entity.PermissionOverwriteEntity
import com.gitlab.kordlib.common.entity.Snowflake

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