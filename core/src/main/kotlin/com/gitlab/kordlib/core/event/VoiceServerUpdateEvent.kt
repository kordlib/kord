package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

class VoiceServerUpdateEvent(
        val token: String,
        val guildId: Snowflake,
        val endpoint: String,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull():Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): VoiceServerUpdateEvent =
            VoiceServerUpdateEvent(token, guildId, endpoint, kord, strategy.supply(kord))
}