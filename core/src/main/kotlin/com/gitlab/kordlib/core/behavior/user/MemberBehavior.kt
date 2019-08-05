package com.gitlab.kordlib.core.behavior.user

import com.gitlab.kordlib.core.`object`.builder.ban.NewBanBuilder
import com.gitlab.kordlib.core.`object`.builder.member.UpdateMemberBuilder
import com.gitlab.kordlib.core.behavior.guild.GuildBehavior
import com.gitlab.kordlib.core.behavior.guild.ban
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

interface MemberBehavior : Entity, UserBehavior {
    val guildId: Snowflake
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    suspend fun unban() = guild.unBan(id)

    suspend fun kick() = guild.kick(id)

    suspend fun addRole(roleId: Snowflake) {
        kord.rest.guild.addRoleToGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value)
    }

    suspend fun removeRole(roleId: Snowflake) {
        kord.rest.guild.deleteRoleFromGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value)
    }

}

suspend inline fun MemberBehavior.ban(builder: NewBanBuilder.() -> Unit) = guild.ban(id, builder)
suspend inline fun MemberBehavior.edit(builder: UpdateMemberBuilder.() -> Unit): Nothing = TODO()
