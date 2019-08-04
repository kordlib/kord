package com.gitlab.kordlib.core.behavior.user

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

interface UserBehavior : Entity {

    suspend fun asMember() : Nothing /*Member*/ = TODO()

    suspend fun getDmChannel() : Nothing /*DmChannel*/ = TODO()

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) : UserBehavior = object : UserBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}