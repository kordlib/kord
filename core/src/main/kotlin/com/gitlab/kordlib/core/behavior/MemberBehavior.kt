package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.cache.api.find
import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.KordCache
import com.gitlab.kordlib.core.cache.data.PresenceData
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.rest.builder.ban.BanCreateBuilder
import com.gitlab.kordlib.rest.builder.member.MemberModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException

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
     * Requests to get the this behavior as a [Member] through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the member wasn't present.
     */
    suspend fun asMember(): Member = strategy.supply(kord).getMember(guildId, id)

    /**
     * Requests to get this behavior as a [Member] through the [strategy],
     * returns null if the member isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun asMemberOrNull(): Member? = strategy.supply(kord).getMemberOrNull(guildId, id)


    /**
     * Requests to unban this member from its guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun unban() = guild.unBan(id)

    /**
     * Requests to kick this member from its guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun kick() = guild.kick(id)

    /**
     * Requests to add the [Role] with the [roleId] to this member.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun addRole(roleId: Snowflake) {
        kord.rest.guild.addRoleToGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value)
    }

    /**
     * Requests to get the [Guild] this member is part of through the [strategy].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    suspend fun getGuild(): Guild = strategy.supply(kord).getGuild(guildId)

    /**
     * Requests to get the [Guild] this member is part of through the [strategy],
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = strategy.supply(kord).getGuildOrNull(guildId)

    /**
     * Requests to remove the [Role] with the [roleId] from this member.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun removeRole(roleId: Snowflake) {
        kord.rest.guild.deleteRoleFromGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value)
    }

    /**
     * Requests to get the [Presence] of this member in the [guild] through the [strategy].
     *
     * This property is not resolvable through REST and will always use the [KordCache] instead.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Presence] wasn't present.
     */
    suspend fun getPresence(): Presence = getPresenceOrNull() ?: EntityNotFoundException.guildEntityNotFound(
            "Presence for Member",
            guildId = guildId,
            id = id
    )

    /**
     * Requests to get the [Presence] of this member in the [guild] through the [strategy],
     * returns null if the [Presence] isn't present.
     *
     * This property is not resolvable through REST and will always use the [KordCache] instead.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getPresenceOrNull(): Presence? {
        val data = kord.cache.query<PresenceData> {
            PresenceData::userId eq id.longValue
            PresenceData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return Presence(data, kord)
    }

    /**
     * Requests to get the [VoiceState] of this member in the [guild] through the [strategy].
     *
     * This property is not resolvable through REST and will always use the [KordCache] instead.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [VoiceState] wasn't present.
     */
    suspend fun getVoiceState(): VoiceState = getVoiceStateOrNull() ?: EntityNotFoundException.guildEntityNotFound(
            "VoiceState for Member",
            guildId = guildId,
            id = id
    )

    /**
     * Requests to get the [VoiceState] of this member in the [guild] through the [strategy],
     * returns null if the [VoiceState] isn't present.
     *
     * This property is not resolvable through REST and will always use the [KordCache] instead.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getVoiceStateOrNull(): VoiceState? {
        val data = kord.cache.find<VoiceStateData> {
            VoiceStateData::userId eq id.longValue
            VoiceStateData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return VoiceState(data, kord)
    }

    /**
     * Returns a new [MemberBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): MemberBehavior = MemberBehavior(guildId = guildId, id = id, kord = kord, strategy = strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy = kord.resources.defaultStrategy): MemberBehavior = object : MemberBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val strategy: EntitySupplyStrategy = strategy
        }
    }

}

/**
 * Requests to ban this member.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun MemberBehavior.ban(builder: BanCreateBuilder.() -> Unit = {}) = guild.ban(id, builder)

/**
 * Requests to edit this member.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun MemberBehavior.edit(builder: MemberModifyBuilder.() -> Unit) {
    kord.rest.guild.modifyGuildMember(guildId.value, id.value, builder)
}
