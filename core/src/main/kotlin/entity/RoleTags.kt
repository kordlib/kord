package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.IntegrationData
import dev.kord.core.cache.data.RoleTagsData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.common.exception.RequestException
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplyStrategy

public class RoleTags(
    public val data: RoleTagsData,
    public val guildId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordObject, Strategizable {

    /**
     * The ID of the bot this belongs to.
     */
    public val botId: Snowflake? get() = data.botId.value

    /**
     * The ID of the of the [Integration] this role belongs to.
     */
    public val integrationId: Snowflake? get() = data.integrationId.value

    /**
     * Whether this is the guild's premium subscriber role.
     */
    public val isPremiumRole: Boolean get() = data.premiumSubscriber

    /**
     * The guild behavior this tag belongs to.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the bot of this tag through the [supplier],
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getBot(): Member? {
        val id = botId ?: return null
        return supplier.getMemberOrNull(guildId, id)
    }

    /**
     * Requests to get the integration of this tag through the [supplier],
     * returns null if the [Integration] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getIntegration(): Integration? {
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
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild of this tag through the [supplier],
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
        RoleTags(data, guildId, kord, strategy.supply(kord))

}
