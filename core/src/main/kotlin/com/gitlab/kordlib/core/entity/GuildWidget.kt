package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.channel.ChannelBehavior
import com.gitlab.kordlib.core.cache.data.GuildWidgetData
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import com.gitlab.kordlib.rest.builder.guild.GuildWidgetModifyBuilder

class GuildWidget(
        val data: GuildWidgetData,
        val guildId: Snowflake,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordObject, Strategizable {

    val isEnabled: Boolean get() = data.enabled

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    val channelId: Snowflake? get() = data.channelId

    val channel: ChannelBehavior? get() = data.channelId?.let { ChannelBehavior(it, kord) }

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    suspend fun getChannelOrNull(): GuildChannel? = data.channelId?.let { supplier.getChannelOfOrNull(it) }

    suspend inline fun <reified T : Channel> getChannelOfOrNull(): T? = data.channelId?.let { supplier.getChannelOfOrNull(it) }

    suspend inline fun edit(builder: GuildWidgetModifyBuilder.() -> Unit): GuildWidget {
        return GuildWidget(GuildWidgetData.from(kord.rest.guild.modifyGuildWidget(guildId, builder)), guildId, kord)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildWidget =
            GuildWidget(data, guildId, kord, strategy.supply(kord))

}