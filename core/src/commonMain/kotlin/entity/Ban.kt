package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.BanData
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of a [Discord Ban](https://discord.com/developers/docs/resources/guild#ban-object).
 */
public class Ban(
    public val data: BanData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {

    /**
     * The id of the banned user.
     */
    public val userId: Snowflake get() = data.userId

    /**
     * The reason for the ban, if present.
     */
    public val reason: String? get() = data.reason

    /**
     * The behavior of the banned user.
     */
    public val user: UserBehavior get() = UserBehavior(id = userId, kord = kord)

    /**
     * Requests to get the [User] that was banned.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [User] wasn't present.
     */
    public suspend fun getUser(): User = supplier.getUser(userId)

    /**
     * Requests to get the [User] that was banned,
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)


    /**
     * Returns a new [Ban] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Ban = Ban(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "Ban(data=$data, kord=$kord, supplier=$supplier)"
    }

}

