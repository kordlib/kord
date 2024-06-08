package dev.kord.core.entity

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.UserFlags
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.UserData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The user's effective name, prioritizing [globalName][User.globalName] over [username][User.username].
 *
 * #### API note:
 *
 * This is implemented as an extension property to avoid virtual dispatch in cases like the following:
 * ```kotlin
 * fun useUser(user: User) = println(user.effectiveName)
 * fun useMember(member: Member) = println(member.effectiveName)
 *
 * val member: Member = TODO()
 * useUser(member) // prints the global display name
 * useMember(member) // prints the guild-specific nickname
 * ```
 */
public val User.effectiveName: String get() = globalName ?: username

/**
 * An instance of a [Discord User](https://discord.com/developers/docs/resources/user#user-object).
 */
public open class User(
    public val data: UserData,
    override val kord: Kord, override val supplier: EntitySupplier = kord.defaultSupplier,
) : UserBehavior {

    override val id: Snowflake
        get() = data.id

    /**
     * Returns true if the user is the same as the bot.
     */
    public val isSelf: Boolean get() = id == kord.selfId
   
    public val avatarHash: String? get() = data.avatar

    /** The avatar of this user as an [Asset]. */
    public val avatar: Asset? get() = avatarHash?.let { Asset.userAvatar(data.id, it, kord) }

    public val defaultAvatar: Asset
        get() =
            if (migratedToNewUsernameSystem) Asset.defaultUserAvatar(userId = id, kord)
            else Asset.defaultUserAvatar(discriminator.toInt(), kord)

    public val avatarDecorationHash: String? get() = data.avatarDecoration.value

    public val avatarDecoration: Asset? get() = avatarDecorationHash?.let { Asset.userAvatarDecoration(data.id, it, kord) }

    /**
     * The username of this user.
     */
    public val username: String get() = data.username

    /**
     * The 4-digit code at the end of the user's discord tag.
     *
     * `"0"` indicates that this user has been migrated to the new username system, see the
     * [Discord Developer Platform](https://discord.com/developers/docs/change-log#unique-usernames-on-discord) for
     * details.
     */
    // "0" when data.discriminator is missing: if the field is missing, all users were migrated,
    // see https://discord.com/developers/docs/change-log#identifying-migrated-users:
    // "After migration of all users is complete, the `discriminator` field may be removed."
    public val discriminator: String get() = data.discriminator.value ?: "0"

    // see https://discord.com/developers/docs/change-log#identifying-migrated-users
    private val migratedToNewUsernameSystem get() = discriminator == "0"

    /** The user's display name, if it is set. For bots, this is the application name. */
    public val globalName: String? get() = data.globalName.value

    override suspend fun asUser(): User {
        return this
    }

    override suspend fun asUserOrNull(): User {
        return this
    }

    /**
     * The flags on a user's account, if present.
     */
    public val publicFlags: UserFlags? get() = data.publicFlags.value

    /**
     * The complete user tag.
     *
     * If this user [has been migrated to the new username system][discriminator], this is the same as [username],
     * otherwise a [String] of the form `"username#discriminator"` is returned.
     */
    public val tag: String get() = if (migratedToNewUsernameSystem) username else "$username#$discriminator"

    /**
     * Whether this user is a bot account.
     */
    public val isBot: Boolean get() = data.bot.discordBoolean

    public val accentColor: Color? get() = data.accentColor?.let { Color(it) }

    public val bannerHash: String? get() = data.banner

    public val banner: Asset? get() = bannerHash?.let { Asset.userBanner(id, it, kord) }

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
