package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.BanData

/**
 * An instance of a [Discord Ban](https://discordapp.com/developers/docs/resources/guild#ban-object).
 */
class Ban(val data: BanData, override val kord: Kord) : KordObject {

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
     * Requests to get the banned user.
     */
    suspend fun getUser(): User = kord.getUser(id = userId)!!

}