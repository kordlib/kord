package dev.kord.core.entity

import dev.kord.common.Color
import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.UserFlags
import dev.kord.common.entity.UserPremium
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.UserData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.Image

/**
 * An instance of a [Discord User](https://discord.com/developers/docs/resources/user#user-object).
 */
open class User(
    val data: UserData,
    override val kord: Kord, override val supplier: EntitySupplier = kord.defaultSupplier,
) : UserBehavior {

    override val id: Snowflake
        get() = data.id

    /**
     * The users avatar as [Icon] object
     */
    val avatar: Icon?
        get() = data.avatar?.let { Icon.UserAvatar(data.id, it, kord) }

    val defaultAvatar: Icon get() = Icon.DefaultUserAvatar(data.discriminator.toInt(), kord)

    /**
     * The username of this user.
     */
    val username: String get() = data.username

    /**
     * The 4-digit code at the end of the user's discord tag.
     */
    val discriminator: String get() = data.discriminator

    /**
     * The flags on a user's account, if present.
     */
    @Deprecated("Use publicFlags instead.", ReplaceWith("publicFlags"), DeprecationLevel.ERROR)
    @DeprecatedSinceKord("0.7.0")
    val flags: UserFlags? by ::publicFlags

    override suspend fun asUser(): User {
        return this
    }

    override suspend fun asUserOrNull(): User {
        return this
    }

    /**
     * The flags on a user's account, if present.
     */
    val publicFlags: UserFlags? get() = data.publicFlags.value

    /**
     * The type of Nitro subscription on a user's account, if present.
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("premiumType is never present.", level = DeprecationLevel.ERROR)
    @DeprecatedSinceKord("0.7.0")
    val premiumType: UserPremium?
        get() = throw NotImplementedError("premiumType is no longer supported.")

    /**
     * The complete user tag.
     */
    val tag: String get() = "$username#$discriminator"

    /**
     * Whether this user is a bot account.
     */
    val isBot: Boolean get() = data.bot.discordBoolean

    val accentColor: Color? get() = data.accentColor?.let { Color(it) }

    fun getBannerUrl(format: Image.Format): String? =
        data.banner?.let { "https://cdn.discordapp.com/banners/${id.asString}/$it.${format.extension}" }


    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean = when (other) {
        is UserBehavior -> other.id == id
        else -> false
    }

    /**
     * Returns a new [User] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): User = User(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "User(data=$data, kord=$kord, supplier=$supplier)"
    }

}
