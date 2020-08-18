package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.IntegrationData
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.toInstant
import com.gitlab.kordlib.rest.builder.integration.IntegrationModifyBuilder
import com.gitlab.kordlib.rest.json.response.IntegrationExpireBehavior
import com.gitlab.kordlib.rest.request.RestRequestException
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * A [Discord integration](https://discordapp.com/developers/docs/resources/guild#get-guild-integrations).
 */
class Integration(
        val data: IntegrationData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Entity, Strategizable {

    override val id: Snowflake
        get() = Snowflake(data.id)

    /**
     * The name of this integration.
     */
    val name: String
        get() = data.name

    /**
     * The type of integration. (`"twitch"`, `"youtube"`, etc)
     */
    val type: String
        get() = data.type

    /**
     * Whether this integration is currently active.
     */
    val isEnabled: Boolean
        get() = data.enabled

    /**
     * Whether this integrations is syncing.
     */
    val isSyncing: Boolean
        get() = data.syncing

    /**
     * The id of the [guild][Guild] this integration is tied to.
     */
    val guildId: Snowflake
        get() = Snowflake(data.guildId)

    /**
     * The behavior of the [guild][Guild] this integration is tied to.
     */
    val guild: GuildBehavior
        get() = GuildBehavior(id = guildId, kord = kord)

    /**
     * The id of the [role][Role] used for 'subscribers' of the integration.
     */
    val roleId: Snowflake
        get() = Snowflake(data.id)

    /**
     * The behavior of the [role][Role] used for 'subscribers' of the integration.
     */
    val role: RoleBehavior
        get() = RoleBehavior(guildId = guildId, id = roleId, kord = kord)


    /**
     * Whether this integration requires emoticons to be synced, only supports Twitch right now.
     */
    val enablesEmoticons: Boolean
        get() = data.enableEmoticons

    /**
     * The behavior used to expire subscribers.
     */
    val expireBehavior: IntegrationExpireBehavior
        get() = data.expireBehavior

    /**
     * The grace period in days before expiring subscribers.
     */
    val expireGracePeriod: Duration
        get() = Duration.of(data.expireGracePeriod.toLong(), ChronoUnit.DAYS)

    /**
     * The id of the [user][User] for this integration.
     */
    val userId: Snowflake
        get() = Snowflake(data.id)

    /**
     * The behavior of the [user][User] for this integration.
     */
    val user: UserBehavior
        get() = UserBehavior(id = userId, kord = kord)

    /**
     * When this integration was last synced.
     */
    val syncedAt: Instant
        get() = data.syncedAt.toInstant()

    /**
     * Requests to get the guild this integration is tied to.
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the guild isn't present.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the guild this integration is tied to, returns null if the guild isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to get the role used for 'subscribers' of the integration.
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the role isn't present.
     */
    suspend fun getRole(): Role = supplier.getRole(guildId = guildId, roleId = roleId)

    /**
     * Requests to get the role used for 'subscribers' of the integration,
     * returns null if the role isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun getRoleOrNull(): Role? = supplier.getRoleOrNull(guildId = guildId, roleId = roleId)

    /**
     * Requests to delete the integration.
     */
    suspend fun delete() {
        kord.rest.guild.deleteGuildIntegration(guildId = guildId.value, integrationId = id.value)
    }

    /**
     * Request to sync an integration.
     */
    suspend fun sync() = kord.rest.guild.syncGuildIntegration(guildId = guildId.value, integrationId = id.value)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Integration =
            Integration(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is Integration -> other.id == id && other.guildId == guildId
        else -> false
    }
}

/**
 * Requests to edit this integration.
 *
 * @return The edited [Integration].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun Integration.edit(builder: IntegrationModifyBuilder.() -> Unit) {
    kord.rest.guild.modifyGuildIntegration(guildId.value, id.value, builder)
}


