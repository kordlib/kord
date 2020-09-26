package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.DiscordTeam
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.ApplicationInfoData
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.Image
import java.util.*

/**
 * The details of a [Discord OAuth2](https://discord.com/developers/docs/topics/oauth2) application.
 */
class ApplicationInfo(
        val data: ApplicationInfoData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Entity, Strategizable {

    override val id: Snowflake
        get() = Snowflake(data.id)

    val name: String get() = data.name

    val description: String? get() = data.description

    val isPublic: Boolean get() = data.botPublic

    val requireCodeGrant: Boolean get() = data.botRequireCodeGrant

    val ownerId: Snowflake get() = Snowflake(data.ownerId)

    val owner: UserBehavior get() = UserBehavior(ownerId, kord)

    val summary: String get() = data.summary

    val verifyKey: String get() = data.verifyKey

    val team: DiscordTeam? = data.team

    val teamId: Long? get() = team?.id?.toLong()

    val guildId: Long? get() = data.guildId

    val primarySkuId: Long? get() = data.primarySkuId

    val slug: String? get() = data.slug

    val coverImage: String? get() = data.coverImage

    suspend fun getOwner(): User = supplier.getUser(ownerId)

    suspend fun getOwnerOrNull(): User? = supplier.getUserOrNull(ownerId)

    suspend fun getCoverImage(): Image? = coverImage?.let { Image.fromUrl(kord.resources.httpClient, it) }

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