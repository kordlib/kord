package dev.kord.core.entity

import dev.kord.common.entity.ApplicationFlags
import dev.kord.common.entity.InstallParams
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.ApplicationData
import dev.kord.core.cache.data.BaseApplicationData
import dev.kord.core.cache.data.PartialApplicationData
import dev.kord.core.event.guild.InviteCreateEvent
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

@Deprecated(
    "'ApplicationInfo' was renamed to 'Application'.",
    ReplaceWith("Application", "dev.kord.core.entity.Application"),
    DeprecationLevel.ERROR,
)
public typealias ApplicationInfo = Application


public sealed class BaseApplication(
    final override val kord: Kord,
    final override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordEntity, Strategizable {
    public abstract val data: BaseApplicationData

    final override val id: Snowflake get() = data.id

    public val name: String get() = data.name

    public val iconHash: String? get() = data.icon

    public val description: String get() = data.description

    /**
     * The rpc origins of this application, empty if disabled.
     */
    public val rpcOrigins: List<String> get() = data.rpcOrigins.orEmpty()

    public val termsOfServiceUrl: String? get() = data.termsOfServiceUrl.value

    public val privacyPolicyUrl: String? get() = data.privacyPolicyUrl.value

    public val ownerId: Snowflake? get() = data.ownerId.value

    public val owner: UserBehavior? get() = ownerId?.let { UserBehavior(it, kord) }

    public val verifyKey: String get() = data.verifyKey

    public val guildId: Snowflake? get() = data.guildId.value

    public val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    public val primarySkuId: Snowflake? get() = data.primarySkuId.value

    public val slug: String? get() = data.slug.value

    public val coverImageHash: String? get() = data.coverImage.value

    public val flags: ApplicationFlags? get() = data.flags.value

    /** Tags describing the content and functionality of the application. */
    public val tags: List<String> get() = data.tags.value.orEmpty()

    /** Settings for the application's default in-app authorization link, if enabled. */
    public val installParams: InstallParams? get() = data.installParams.value

    /** The application's default custom authorization link, if enabled. */
    public val customInstallUrl: String? get() = data.customInstallUrl.value


    public suspend fun getOwnerOrNull(): User? = ownerId?.let { supplier.getUserOrNull(it) }

    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }


    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): BaseApplication


    final override fun hashCode(): Int = Objects.hash(id)

    final override fun equals(other: Any?): Boolean = other is BaseApplication && this.id == other.id
}


/**
 * The details of an
 * [Application](https://discord.com/developers/docs/resources/application#application-object-application-structure).
 */
public class Application(
    public override val data: ApplicationData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
) : BaseApplication(kord, supplier) {

    public val isPublic: Boolean get() = data.botPublic

    public val requireCodeGrant: Boolean get() = data.botRequireCodeGrant

    public val teamId: Snowflake? get() = data.team?.id

    public val team: Team? get() = data.team?.let { Team(it, kord) }

    @Deprecated(
        "'ownerId' might not be present, use 'getOwnerOrNull' instead.",
        ReplaceWith("this.getOwnerOrNull()"),
        DeprecationLevel.ERROR,
    )
    public suspend fun getOwner(): User = supplier.getUser(ownerId!!)

    /**
     * Returns a new [Application] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Application =
        Application(data, kord, strategy.supply(kord))

    override fun toString(): String = "Application(data=$data, kord=$kord, supplier=$supplier)"
}


/**
 * The partial details of an
 * [Application](https://discord.com/developers/docs/resources/application#application-object-application-structure)
 * sent in [InviteCreateEvent]s.
 */
public class PartialApplication(
    public override val data: PartialApplicationData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier,
) : BaseApplication(kord, supplier) {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PartialApplication =
        PartialApplication(data, kord, strategy.supply(kord))

    override fun toString(): String = "PartialApplication(data=$data, kord=$kord, supplier=$supplier)"
}
