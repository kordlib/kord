package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.BanData
import com.gitlab.kordlib.core.exception.EntityNotFoundException

/**
 * An instance of a [Discord Ban](https://discordapp.com/developers/docs/resources/guild#ban-object).
 */
class Ban(
        val data: BanData,
        override val kord: Kord,
        override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
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
     * Requests to get the [User] that was banned through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [User] wasn't present.
     */
    suspend fun getUser(): User = strategy.supply(kord).getUser(userId)

    /**
     * Requests to get the [User] that was banned through the [strategy],
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getUserOrNull(): User? = strategy.supply(kord).getUserOrNull(userId)


    /**
     * Returns a new [Ban] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy) : Ban = Ban(data, kord, strategy)

}

