package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

class BanAddEvent(
        val user: User,
        val guildId: Snowflake,
        override val shard: Int,
        override val supplier: EntitySupplier = user.kord.defaultSupplier
) : Event, Strategizable {

    override val kord: Kord get() = user.kord

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the [Guild] this ban happened in.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the  wasn't present.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] this ban happened in,
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): BanAddEvent =
            BanAddEvent(user, guildId, shard, strategy.supply(kord))
}
