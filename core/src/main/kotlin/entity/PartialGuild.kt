package dev.kord.core.entity

import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
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
import java.util.*

class PartialGuild(
        val data: PartialGuildData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Entity, Strategizable {

    /**
     * The name of this guild.
     */
    val name: String get() = data.name


    override val id: Snowflake get() = data.id

    /**
     * The icon hash, if present.
     */
    val iconHash: String? get() = data.icon

    /**
     * wither who created the invite is the owner or not.
     */

    val owner: Boolean? get() = data.owner.value


    /**
     * The welcome screen of a Community guild, shown to new members.
     */

    val welcomeScreen: WelcomeScreen? get() = data.welcomeScreen.unwrap { WelcomeScreen(it, kord) }


    /**
     * permissions that the invite creator has, if present.
     */

    val permissions: Permissions? get() = data.permissions.value

    /**
     * Gets the icon url, if present.
     */
    fun getIconUrl(format: Image.Format): String? = data.icon?.let { "https://cdn.discordapp.com/icons/${id.asString}/$it.${format.extension}" }

    /**
     * Requests to get the icon image in the specified [format], if present.
     */
    suspend fun getIcon(format: Image.Format): Image? {
        val url = getIconUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }

    /**
     * Requests to get the full [Guild] entity  for this [PartialGuild].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present, or the bot is not a part of this [Guild].
     */
    suspend fun getGuild(): Guild = supplier.getGuild(id)

    /**
     * Requests to get the [Guild] for this invite,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(id)


    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildBehavior -> other.id == id
        is PartialGuild -> other.id == id
        else -> false
    }


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PartialGuild =
            PartialGuild(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "PartialGuild(data=$data, kord=$kord, supplier=$supplier)"
    }

}