package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.PresenceData
import com.gitlab.kordlib.core.cache.data.VoiceStateData
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.builder.ban.BanCreateBuilder
import com.gitlab.kordlib.rest.builder.member.MemberModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Member](https://discord.com/developers/docs/resources/guild#guild-member-object).
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
     * Requests to get the this behavior as a [Member].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the member wasn't present.
     */
    suspend fun asMember(): Member = supplier.getMember(guildId, id)

    /**
     * Requests to get this behavior as a [Member],
     * returns null if the member isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun asMemberOrNull(): Member? = supplier.getMemberOrNull(guildId, id)


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
    suspend fun kick(reason: String? = null) = guild.kick(id, reason)

    /**
     * Requests to add the [Role] with the [roleId] to this member.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun addRole(roleId: Snowflake) {//TODO remove in Kord 0.7.0
        kord.rest.guild.addRoleToGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value)
    }

    /**
     * Requests to add the [Role] with the [roleId] to this member.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun addRole(roleId: Snowflake, reason: String? = null) {
        kord.rest.guild.addRoleToGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value, reason = reason)
    }

    /**
     * Requests to get the [Guild] this member is part of.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] this member is part of,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to remove the [Role] with the [roleId] from this member.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun removeRole(roleId: Snowflake) {//TODO remove in Kord 0.7.0
        kord.rest.guild.deleteRoleFromGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value)
    }

    /**
     * Requests to remove the [Role] with the [roleId] from this member.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun removeRole(roleId: Snowflake, reason: String) {
        kord.rest.guild.deleteRoleFromGuildMember(guildId = guildId.value, userId = id.value, roleId = roleId.value, reason = reason)
    }

    /**
     * Requests to get the [Presence] of this member in the [guild].
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
     * Requests to get the [Presence] of this member in the [guild],
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
     * Requests to get the [VoiceState] of this member in the [guild].
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
     * Requests to get the [VoiceState] of this member in the [guild],
     * returns null if the [VoiceState] isn't present.
     *
     * This property is not resolvable through REST and will always use the [KordCache] instead.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getVoiceStateOrNull(): VoiceState? {
        val data = kord.cache.query<VoiceStateData> {
            VoiceStateData::userId eq id.longValue
            VoiceStateData::guildId eq guildId.longValue
        }.singleOrNull() ?: return null

        return VoiceState(data, kord)
    }

    /**
     * Returns a new [MemberBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MemberBehavior = MemberBehavior(guildId = guildId, id = id, kord = kord, strategy = strategy)

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy): MemberBehavior = object : MemberBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)

            override fun hashCode(): Int = Objects.hash(id, guildId)

            override fun equals(other: Any?): Boolean = when(other) {
                is MemberBehavior -> other.id == id && other.guildId == guildId
                is UserBehavior -> other.id == id
                else -> false
            }
        }
    }

}

/**
 * Requests to ban this member.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun MemberBehavior.ban(builder: BanCreateBuilder.() -> Unit = {}) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    guild.ban(id, builder)
}

/**
 * Requests to edit this member.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun MemberBehavior.edit(builder: MemberModifyBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.guild.modifyGuildMember(guildId.value, id.value, builder)
}
