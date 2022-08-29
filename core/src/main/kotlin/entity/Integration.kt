package dev.kord.core.entity

import dev.kord.common.entity.IntegrationExpireBehavior
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.IntegrationData
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.integration.IntegrationModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlinx.datetime.Instant
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

/**
 * A [Discord integration](https://discord.com/developers/docs/resources/guild#get-guild-integrations).
 */
public class Integration(
    public val data: IntegrationData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity, Strategizable {

    override val id: Snowflake
        get() = data.id

    /**
     * The name of this integration.
     */
    public val name: String
        get() = data.name

    /**
     * The type of integration. (`"twitch"`, `"youtube"`, etc)
     */
    public val type: String
        get() = data.type

    /**
     * Whether this integration is currently active.
     */
    public val isEnabled: Boolean
        get() = data.enabled

    /**
     * Whether this integrations is syncing.
     */
    public val isSyncing: Boolean
        get() = data.syncing

    /**
     * The id of the [guild][Guild] this integration is tied to.
     */
    public val guildId: Snowflake
        get() = data.guildId

    /**
     * The behavior of the [guild][Guild] this integration is tied to.
     */
    public val guild: GuildBehavior
        get() = GuildBehavior(id = guildId, kord = kord)

    /**
     * The id of the [role][Role] used for 'subscribers' of the integration.
     */
    public val roleId: Snowflake
        get() = data.id

    /**
     * The behavior of the [role][Role] used for 'subscribers' of the integration.
     */
    public val role: RoleBehavior
        get() = RoleBehavior(guildId = guildId, id = roleId, kord = kord)


    /**
     * Whether this integration requires emoticons to be synced, only supports Twitch right now.
     */
    public val enablesEmoticons: Boolean
        get() = data.enableEmoticons.orElse(false)

    /**
     * The behavior used to expire subscribers.
     */
    public val expireBehavior: IntegrationExpireBehavior
        get() = data.expireBehavior

    /**
     * The grace period before expiring subscribers.
     */
    public val expireGracePeriod: Duration get() = data.expireGracePeriod

    /**
     * The id of the [user][User] for this integration.
     */
    public val userId: Snowflake
        get() = data.id

    /**
     * The behavior of the [user][User] for this integration.
     */
    public val user: UserBehavior
        get() = UserBehavior(id = userId, kord = kord)

    /**
     * When this integration was last synced.
     */
    public val syncedAt: Instant get() = data.syncedAt

    /**
     * Requests to get the guild this integration is tied to.
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the guild isn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild this integration is tied to, returns null if the guild isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the role used for 'subscribers' of the integration.
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the role isn't present.
     */
    public suspend fun getRole(): Role = supplier.getRole(guildId = guildId, roleId = roleId)

    /**
     * Requests to get the role used for 'subscribers' of the integration,
     * returns null if the role isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getRoleOrNull(): Role? = supplier.getRoleOrNull(guildId = guildId, roleId = roleId)

    /**
     * Requests to delete the integration.
     *
     * @param reason the reason showing up in the audit log
     */
    public suspend fun delete(reason: String? = null) {
        kord.rest.guild.deleteGuildIntegration(guildId = guildId, integrationId = id, reason = reason)
    }

    /**
     * Request to sync an integration.
     */
    public suspend fun sync(): Unit = kord.rest.guild.syncGuildIntegration(guildId = guildId, integrationId = id)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Integration =
        Integration(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is Integration -> other.id == id && other.guildId == guildId
        else -> false
    }

    override fun toString(): String {
        return "Integration(data=$data, kord=$kord, supplier=$supplier)"
    }

}

/**
 * Requests to edit this integration.
 *
 * @return The edited [Integration].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun Integration.edit(builder: IntegrationModifyBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.guild.modifyGuildIntegration(guildId = guildId, integrationId = id, builder)
}
