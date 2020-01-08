package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.cache.data.PermissionOverwriteData
import com.gitlab.kordlib.core.entity.channel.GuildChannel

class PermissionOverwriteEntity(
        val guildId: Snowflake,
        val channelId: Snowflake,
        data: PermissionOverwriteData,
        override val kord: Kord
) : PermissionOverwrite(data), KordObject {

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)
    val channel: GuildChannelBehavior get() = GuildChannelBehavior(guildId, channelId, kord)

    suspend fun getChannel(): GuildChannel? = kord.getChannel(channelId) as? GuildChannel
    suspend fun getGuild(): Guild? = kord.getGuild(guildId)

    suspend fun delete(reason: String? = null) = kord.rest.channel.deleteChannelPermission(channelId.value, data.id.toString(), reason)

}
