package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.cache.data.InviteData
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.indexOfFirstOrNull
import com.gitlab.kordlib.rest.builder.channel.ChannelPermissionModifyBuilder
import com.gitlab.kordlib.rest.service.editRolePermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

/**
 * The behavior of a Discord channel associated to a [guild].
 */
interface GuildChannelBehavior : ChannelBehavior {
    /**
     * The id of the guild this channel is associated to.
     */
    val guildId: Snowflake

    /**
     * The guild behavior this channel is associated to.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the invites of this channel.
     */
    val invites: Flow<Invite> get() = flow {
        val responses = kord.rest.channel.getChannelInvites(id.value)

        for (response in responses) {
            val data = InviteData.from(response)

            emit(Invite(data, kord))
        }
    }

    override suspend fun asChannel(): GuildChannel {
        return super.asChannel() as GuildChannel
    }

    /**
     * Requests to get this behavior as a [Guild].
     */
    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

    /**
     * Requests to add or replace a [PermissionOverwrite] to this entity.
     */
    suspend fun addOverwrite(overwrite: PermissionOverwrite) {
        kord.rest.channel.editChannelPermissions(channelId = id.value, overwriteId = overwrite.target.value, permissions = overwrite.asRequest())
    }

    /**
     * Requests to get the position of this channel in the [guild], as displayed in Discord.
     */
    suspend fun getPosition(): Int = guild.channels.indexOfFirstOrNull { it.id == id }!!

    override fun compareTo(other: Entity): Int {
        if (other !is GuildChannelBehavior) return super.compareTo(other)
        val discordOrder = compareBy<GuildChannelBehavior> { it.guildId }
                .thenBy { (it as? GuildChannel)?.guildId }
                .thenBy { it.id }

        return discordOrder.compare(this, other)
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord) = object : GuildChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord

            override fun hashCode(): Int = Objects.hash(id, guildId)

            override fun equals(other: Any?): Boolean = when(other) {
                is GuildChannelBehavior -> other.id == id && other.guildId == guildId
                is ChannelBehavior -> other.id == id
                else -> false
            }
        }
    }

}

/**
 * Requests to add or replace a [PermissionOverwrite] for the [roleId].
 */
suspend inline fun GuildChannelBehavior.editRolePermission(roleId: Snowflake, builder: ChannelPermissionModifyBuilder.() -> Unit) {
    kord.rest.channel.editRolePermission(channelId = id.value, roleId = roleId.value, builder = builder)
}

/**
 * Requests to add or replace a [PermissionOverwrite] for the [memberId].
 */
suspend inline fun GuildChannelBehavior.editMemberPermission(memberId: Snowflake, builder: ChannelPermissionModifyBuilder.() -> Unit) {
    kord.rest.channel.editRolePermission(channelId = id.value, roleId = memberId.value, builder = builder)
}
