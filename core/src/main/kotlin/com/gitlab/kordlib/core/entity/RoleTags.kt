package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.cache.data.IntegrationData
import com.gitlab.kordlib.core.cache.data.RoleTagsData
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

class RoleTags(
        val data: RoleTagsData,
        val guildId: Snowflake,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordObject, Strategizable {

    /**
     * The ID of the bot this belongs to.
     */
    val botId: Snowflake? get() = data.botId.value

    /**
     * The ID of the of the [Integration] this role belongs to.
     */
    val integrationId: Snowflake? get() = data.integrationId.value

    /**
     * Whether this is the guild's premium subscriber role.
     */
    val isPremiumRole: Boolean get() = data.premiumSubscriber

    /**
     * The guild behavior this tag belongs to.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the bot of this tag through the [supplier],
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getBot(): Member? {
        val id = botId ?: return null
        return supplier.getMemberOrNull(guildId, id)
    }

    /**
     * Requests to get the integration of this tag through the [supplier],
     * returns null if the [Integration] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getIntegration(): Integration? {
        val id = integrationId ?: return null
        val response = kord.rest.guild.getGuildIntegrations(guildId)
                .firstOrNull { it.id == id } ?: return null

        return Integration(IntegrationData.from(guildId, response), kord)
    }

    /**
     * Requests to get the guild of this tag through the [supplier].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild of this tag through the [supplier],
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
            RoleTags(data, guildId, kord, strategy.supply(kord))

}