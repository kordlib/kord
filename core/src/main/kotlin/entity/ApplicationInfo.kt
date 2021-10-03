package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.coerceToMissing
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.ApplicationInfoData
import dev.kord.core.cache.data.TeamData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * The details of a [Discord OAuth2](https://discord.com/developers/docs/topics/oauth2) application.
 */
public class ApplicationInfo(
    public val data: ApplicationInfoData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordEntity, Strategizable {

    override val id: Snowflake
        get() = data.id

    public val name: String get() = data.name

    public val description: String get() = data.description

    public val isPublic: Boolean get() = data.botPublic

    public val requireCodeGrant: Boolean get() = data.botRequireCodeGrant

    /**
     * The rpc origins of this application, empty if disabled.
     */
    public val rpcOrigins: List<String> get() = data.rpcOrigins.coerceToMissing().orEmpty()

    public val ownerId: Snowflake get() = data.ownerId

    public val owner: UserBehavior get() = UserBehavior(ownerId, kord)

    public val summary: String get() = data.summary

    public val verifyKey: String get() = data.verifyKey

    public val teamId: Snowflake? get() = data.team?.id

    public val team: Team? get() = data.team?.let { Team(TeamData.from(it), kord) }

    public val guildId: Snowflake? get() = data.guildId.value

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public val primarySkuId: Snowflake? get() = data.primarySkuId.value

    public val slug: String? get() = data.slug.value

    public val coverImageHash: String? get() = data.coverImage.value

    public suspend fun getOwner(): User = supplier.getUser(ownerId)

    public suspend fun getOwnerOrNull(): User? = supplier.getUserOrNull(ownerId)

    public suspend fun getGuildOrNull(): Guild? {
        return supplier.getGuildOrNull(guildId ?: return null)
    }

    /**
     * Returns a new [ApplicationInfo] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): ApplicationInfo =
        ApplicationInfo(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is ApplicationInfo -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "ApplicationInfo(data=$data, kord=$kord, supplier=$supplier)"
    }

}
