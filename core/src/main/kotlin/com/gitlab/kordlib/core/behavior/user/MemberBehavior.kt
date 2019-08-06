package com.gitlab.kordlib.core.behavior.user

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.ban.NewBanBuilder
import com.gitlab.kordlib.core.`object`.builder.member.UpdateMemberBuilder
import com.gitlab.kordlib.core.behavior.guild.GuildBehavior
import com.gitlab.kordlib.core.behavior.guild.ban
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

/**
 * The behavior of a [Discord Member](https://discordapp.com/developers/docs/resources/guild#guild-member-object).
 */
interface MemberBehavior : Entity, UserBehavior {
    /**
     * The id of the guild this channel is associated to.
     */
    val guildId: Snowflake

    /**
     * The guild this channel is associated to.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to unban this member from its guild.
     */
    suspend fun unban() = guild.unBan(id)

    /**
     * Requests to kick this member from its guild.
     */
    suspend fun kick() = guild.kick(id)

    /**
     * Requests to add a [role][roleId]  to this member.
     */
    suspend fun addRole(roleId: Snowflake) {
        kord.rest.guild.addRoleToGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value)
    }

    /**
     * Requests to remove a [role][roleId] from this member.
     */
    suspend fun removeRole(roleId: Snowflake) {
        kord.rest.guild.deleteRoleFromGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value)
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord): MemberBehavior = object: MemberBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

/**
 * Requests to ban this member.
 */
suspend inline fun MemberBehavior.ban(builder: NewBanBuilder.() -> Unit) = guild.ban(id, builder)

/**
 * Requests to edit this member.
 *
 * @return The edited [Member].
 */
suspend inline fun MemberBehavior.edit(builder: UpdateMemberBuilder.() -> Unit): Nothing = TODO()
