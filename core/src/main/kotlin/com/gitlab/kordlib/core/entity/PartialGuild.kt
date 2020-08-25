package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.cache.data.PartialGuildData
import com.gitlab.kordlib.core.entity.channel.Channel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import com.gitlab.kordlib.rest.Image
import java.util.*

@OptIn(KordUnstableApi::class)
class PartialGuild(
        val data: PartialGuildData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Entity, Strategizable {

    /**
     * The name of this guild.
     */
    val name: String get() = data.name


    override val id: Snowflake get() = Snowflake(data.id)

    /**
     * The icon hash, if present.
     */
    val iconHash: String? get() = data.icon

    /**
     * wither who created the invite is the owner or not.
     */

    val owner: Boolean? get() = data.owner

    /**
     * permissions that the invite creator has, if present.
     */

    val permissions: Permissions? get() = data.permissions

    /**
     * Gets the icon url, if present.
     */
    fun getIconUrl(format: Image.Format): String? = data.icon?.let { "https://cdn.discordapp.com/icons/${id.value}/$it.${format.extension}" }

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

}