package com.gitlab.kordlib.core.behavior.user

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

/**
 * The behavior of a [Discord User](https://discordapp.com/developers/docs/resources/user)
 */
interface UserBehavior : Entity {

    /**
     * Requests this user as a member of the [guild][guildId].
     */
    suspend fun asMember(guildId: Snowflake) : Nothing /*Member*/ = TODO()

    /**
     * Requests to get or create a [DMChannel] between this bot and the user.
     */
    suspend fun getDmChannel() : Nothing /*DmChannel*/ = TODO()

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) : UserBehavior = object : UserBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}