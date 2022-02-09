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
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.Image
import dev.kord.rest.service.RestClient
import java.util.*

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

    /**
     * wither who created the invite is the owner or not.
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

    public val splashHash: String? get() = data.splash.value


    /**
     * Gets the discovery splash url in the specified [format], if present.
     */
    public fun getDiscoverySplashUrl(format: Image.Format): String? =
        splashHash?.let { "discovery-splashes/$id/${it}.${format.extension}" }

    /**
     * Requests to get the splash image in the specified [format], if present.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     */
    public suspend fun getDiscoverySplash(format: Image.Format): Image? {
        val url = getDiscoverySplashUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }


    /**
     * Gets the icon url, if present.
     */
    public fun getIconUrl(format: Image.Format): String? =
        iconHash?.let { "https://cdn.discordapp.com/icons/$id/$it.${format.extension}" }


    /**
     * Requests to get the icon image in the specified [format], if present.
     */
    public suspend fun getIcon(format: Image.Format): Image? {
        val url = getIconUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }


    /**
     * Gets the banner url in the specified format.
     */
    public fun getBannerUrl(format: Image.Format): String? =
        data.banner.value?.let { "https://cdn.discordapp.com/banners/$id/$it.${format.extension}" }

    /**
     * Requests to get the banner image in the specified [format], if present.
     */
    public suspend fun getBanner(format: Image.Format): Image? {
        val url = getBannerUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }


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


    override fun hashCode(): Int = Objects.hash(id)

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
