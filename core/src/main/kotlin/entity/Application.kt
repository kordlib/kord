package dev.kord.core.entity

import dev.kord.common.entity.ApplicationFlags
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.ApplicationData
import dev.kord.core.cache.data.TeamData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

@Deprecated(
    "'ApplicationInfo' was renamed to 'Application'.",
    ReplaceWith("Application", "dev.kord.core.entity.Application"),
    DeprecationLevel.ERROR,
)
public typealias ApplicationInfo = Application

/**
 * The details of an
 * [Application](https://discord.com/developers/docs/resources/application#application-object-application-structure).
 */
public class Application(
    public val data: ApplicationData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordEntity, Strategizable {

    override val id: Snowflake
        get() = data.id

    public val name: String get() = data.name

    public val iconHash: String? get() = data.icon

    public val description: String get() = data.description

    public val isPublic: Boolean get() = data.botPublic

    public val requireCodeGrant: Boolean get() = data.botRequireCodeGrant

    /**
     * The rpc origins of this application, empty if disabled.
     */
    public val rpcOrigins: List<String> get() = data.rpcOrigins.orEmpty()

    public val termsOfServiceUrl: String? get() = data.termsOfServiceUrl.value

    public val privacyPolicyUrl: String? get() = data.privacyPolicyUrl.value

    public val ownerId: Snowflake? get() = data.ownerId.value

    public val owner: UserBehavior? get() = ownerId?.let { UserBehavior(it, kord) }

    public val summary: String get() = data.summary

    public val verifyKey: String get() = data.verifyKey

    public val teamId: Snowflake? get() = data.team?.id

    public val team: Team? get() = data.team?.let { Team(TeamData.from(it), kord) }

    public val guildId: Snowflake? get() = data.guildId.value

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public val primarySkuId: Snowflake? get() = data.primarySkuId.value

    public val slug: String? get() = data.slug.value

    public val coverImageHash: String? get() = data.coverImage.value

    public val flags: ApplicationFlags? get() = data.flags.value

    @Deprecated(
        "'ownerId' might not be present, use 'getOwnerOrNull' instead.",
        ReplaceWith("this.getOwnerOrNull()"),
        DeprecationLevel.ERROR,
    )
    public suspend fun getOwner(): User = supplier.getUser(ownerId!!)

    public suspend fun getOwnerOrNull(): User? = ownerId?.let { supplier.getUserOrNull(it) }

    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    /**
     * Returns a new [Application] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Application =
        Application(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is Application -> other.id == id
        is PartialApplication -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Application(data=$data, kord=$kord, supplier=$supplier)"
    }

}