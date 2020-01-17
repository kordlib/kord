package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildMessageChannelBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel

class WebhookUpdateEvent(
        val guildId: Snowflake,
        val channelId: Snowflake,
        override val kord: Kord
) : Event {

    val channel: GuildMessageChannelBehavior get() = GuildMessageChannelBehavior(guildId, channelId, kord)

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getChannel(): GuildMessageChannel = kord.getChannel(channelId) as GuildMessageChannel

    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!
}