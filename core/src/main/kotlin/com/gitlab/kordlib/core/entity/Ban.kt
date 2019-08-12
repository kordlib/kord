package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.`object`.data.BanData
import com.gitlab.kordlib.core.behavior.user.UserBehavior

class Ban(val data: BanData, override val kord: Kord) : KordObject {
    val userId: Snowflake get() = Snowflake(data.user.id)

    val user get() = UserBehavior(id = userId, kord = kord)

    suspend fun getUser(): User = kord.getUser(userId = userId)!!
}