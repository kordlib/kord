package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.ApplicationInfoData

class ApplicationInfo(val data: ApplicationInfoData, override val kord: Kord) : Entity {

    override val id: Snowflake
        get() = Snowflake(data.id)

    val name: String get() = data.name

    val description: String? get() = data.description

    val isPublic: Boolean get() = data.botPublic

    val requireCodeGrant: Boolean get() = data.botRequireCodeGrant

    val ownerId: Snowflake get() = Snowflake(data.ownerId)

    val owner: UserBehavior get() = UserBehavior(ownerId, kord)

    suspend fun getOwner(): User = kord.getUser(ownerId)!!
}