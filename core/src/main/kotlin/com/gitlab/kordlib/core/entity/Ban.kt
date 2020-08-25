package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.BanData
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy

/**
 * An instance of a [Discord Ban](https://discord.com/developers/docs/resources/guild#ban-object).
 */
@OptIn(KordUnstableApi::class)
class Ban(
        val data: BanData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {

    /**
     * The id of the banned user.
     */
    val userId: Snowflake get() = Snowflake(data.userId)

    /**
     * The reason for the ban, if present.
     */
    val reason: String? get() = data.reason

    /**
     * The behavior of the banned user.
     */
    val user: UserBehavior get() = UserBehavior(id = userId, kord = kord)

    /**
     * Requests to get the [User] that was banned.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [User] wasn't present.
     */
    suspend fun getUser(): User = supplier.getUser(userId)

    /**
     * Requests to get the [User] that was banned,
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)


    /**
     * Returns a new [Ban] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>) : Ban = Ban(data, kord, strategy.supply(kord))

}

