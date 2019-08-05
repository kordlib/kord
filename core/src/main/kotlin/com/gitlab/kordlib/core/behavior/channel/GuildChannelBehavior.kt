package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.PermissionOverwrite
import com.gitlab.kordlib.core.behavior.guild.GuildBehavior
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.flow.Flow

/**
 * A Discord channel associated to a [guild]
 */
interface GuildChannelBehavior : ChannelBehavior {
    /**
     * the id of the guild this channel is associated to.
     */
    val guildId: Snowflake

    /**
     * the guild this channel is associated to.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the invites of this channel.
     */
    suspend fun getInvites(): Flow<Nothing /*Invite*/> = TODO()

    /**
     * Requests to add or replace a [PermissionOverwrite] to this entity.
     */
    suspend fun addOverwrite(overwrite: PermissionOverwrite) {
        kord.rest.channel.editChannelPermissions(channelId = id.value, overwriteId = overwrite.target.value, permissions = overwrite.asRequest())
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord) = object : GuildChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

