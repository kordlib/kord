package dev.kord.core.entity

import dev.kord.common.entity.NsfwLevel
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.VerificationLevel
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.entity.optional.value
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.PartialGuildData
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * Represents a [Partial guild object](https://discord.com/developers/docs/resources/guild#unavailable-guild-object).
 *
 * @param data The [PartialGuildData] for the guild
 */
public class PartialGuild(
    public val data: PartialGuildData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : GuildBehavior {

    /**
     * The name of this guild.
     */
    public val name: String get() = data.name


    override val id: Snowflake get() = data.id

    /**
     * The icon hash, if present.
     */
    public val iconHash: String? get() = data.icon

    public val icon: Asset? get() = iconHash?.let { Asset.guildIcon(id, it, kord) }

    /**
     * Whether who created the invite is the owner or not.
     */

    public val owner: Boolean? get() = data.owner.value


    /**
     * The welcome screen of a Community guild, shown to new members.
     */

    public val welcomeScreen: WelcomeScreen? get() = data.welcomeScreen.unwrap { WelcomeScreen(it, kord) }


    /**
     * permissions that the invite creator has, if present.
     */

    public val permissions: Permissions? get() = data.permissions.value


    /**
     * The vanity code of this server used in the [vanityUrl], if present.
     */
    public val vanityCode: String? get() = data.vanityUrlCode.value

    /**
     * The vanity invite URL of this server, if present.
     */
    public val vanityUrl: String? get() = vanityCode?.let { "https://discord.gg/$it" }

    /**
     * The description of this guild, if present.
     */
    public val description: String? get() = data.description.value


    /**
     * The [NSFW Level](https://discord.com/developers/docs/resources/guild#guild-object-guild-nsfw-level) of this Guild
     */
    public val nsfw: NsfwLevel? get() = data.nsfwLevel.value

    /**
     * The verification level required for the guild.
     */
    public val verificationLevel: VerificationLevel? get() = data.verificationLevel.value

    /**
     * The hash for the discovery splash.
     */
    public val splashHash: String? get() = data.splash.value

    public val splash: Asset? get() = splashHash?.let { Asset.guildSplash(id, it, kord) }

    public val bannerHash: String? get() = data.banner.value

    public val banner: Asset? get() = bannerHash?.let { Asset.guildBanner(id, it, kord) }

    /**
     * The approximate number of members in this guild.
     *
     * Present if this guild was requested through [rest][dev.kord.rest.service.RestClient] with the flag `with_counts`.
     */
    public val approximateMemberCount: Int? get() = data.approximateMemberCount.value

    /**
     * The approximate number of online members in this guild.
     *
     * Present if this guild was requested through [rest][dev.kord.rest.service.RestClient] with the flag `with_counts`.
     */
    public val approximatePresenceCount: Int? get() = data.approximatePresenceCount.value


    /**
     * Requests to get the full [Guild] entity  for this [PartialGuild].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present, or the bot is not a part of this [Guild].
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(id)

    /**
     * Requests to get the [Guild] for this invite,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(id)


    override fun hashCode(): Int = hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildBehavior -> other.id == id
        else -> false
    }


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PartialGuild =
        PartialGuild(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "PartialGuild(data=$data, kord=$kord, supplier=$supplier)"
    }

}
