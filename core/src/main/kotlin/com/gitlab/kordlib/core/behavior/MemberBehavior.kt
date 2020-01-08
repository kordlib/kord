package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.rest.builder.ban.BanCreateBuilder
import com.gitlab.kordlib.rest.builder.member.MemberModifyBuilder
import com.gitlab.kordlib.core.cache.data.PresenceData
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.core.entity.*

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
     * The raw mention for this member's nickname.
     */
    val nicknameMention get() = "<@!${id.value}>"

    /**
     * Requests to get this behaviour as a member.
     */
    suspend fun asMember(): Member = kord.getMember(guildId, id)!!

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

    suspend fun getGuild(): Guild = kord.getGuild(guildId)!!

    /**
     * Requests to remove a [role][roleId] from this member.
     */
    suspend fun removeRole(roleId: Snowflake) {
        kord.rest.guild.deleteRoleFromGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value)
    }

    /**
     * Requests to get the cached presence, if cached.
     */
    suspend fun getPresence(): Presence? {
        val data = kord.cache.find<PresenceData> {
            PresenceData::userId eq id.longValue
            PresenceData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return Presence(data, kord)
    }

    /**
     * Requests to get the cached voice state, if present.
     */
    suspend fun getVoiceState(): VoiceState? {
        val data = kord.cache.find<VoiceStateData> {
            VoiceStateData::userId eq id.longValue
            VoiceStateData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return VoiceState(data, kord)
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord): MemberBehavior = object : MemberBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

/**
 * Requests to ban this member.
 */
suspend inline fun MemberBehavior.ban(builder: BanCreateBuilder.() -> Unit = {}) = guild.ban(id, builder)

/**
 * Requests to edit this member.
 */
@Suppress("NAME_SHADOWING")
suspend inline fun MemberBehavior.edit(builder: MemberModifyBuilder.() -> Unit) {
    val builder = MemberModifyBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    kord.rest.guild.modifyGuildMember(guildId.value, id.value, request, reason)
}
