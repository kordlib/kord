package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.data.EmojiData
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.toSnowflakeOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class GuildEmoji(val data: EmojiData, val guildId: Snowflake, override val kord: Kord) : Entity {
    override val id: Snowflake
        get() = Snowflake(data.id)

    val isAnimated: Boolean get() = data.animated

    val isManaged: Boolean get() = data.managed

    val name: String get() = data.name

    val requiresColons: Boolean get() = data.requireColons

    val roleIds: Set<Snowflake> get() = data.roles.asSequence().map { Snowflake(it) }.toSet()

    val roles: Flow<Role> get() = roleIds.asFlow().map { kord.getRole(guildId, id) }.filterNotNull()

    val member: MemberBehavior? get() = userId?.let { MemberBehavior(guildId, it, kord) }

    val userId: Snowflake? get() = data.user?.id.toSnowflakeOrNull()

    val user: UserBehavior? get() = userId?.let { UserBehavior(it, kord) }

    suspend fun getMember(): Member? = userId?.let { kord.getMember(guildId = guildId, userId = it) }

    suspend fun getUser(): User? = userId?.let { kord.getUser(it) }

}