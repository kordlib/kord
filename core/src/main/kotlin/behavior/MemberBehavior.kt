package dev.kord.core.behavior

import dev.kord.cache.api.query
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.PresenceData
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.data.VoiceStateData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.*
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.ban.BanCreateBuilder
import dev.kord.rest.builder.member.MemberModifyBuilder
import dev.kord.rest.request.RestRequestException
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Member](https://discord.com/developers/docs/resources/guild#guild-member-object).
 */
public interface MemberBehavior : KordEntity, UserBehavior {

    /**
     * The id of the guild this channel is associated to.
     */
    public val guildId: Snowflake

    /**
     * The guild this channel is associated to.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * The raw mention for this member's nickname.
     */
    @Deprecated(
        "Nickname mentions are deprecated and should be handled the same way as regular user mentions, " +
                "see https://discord.com/developers/docs/reference#message-formatting-formats",
        ReplaceWith("this.mention"),
    )
    public val nicknameMention: String get() = "<@!$id>"

    /**
     * Requests to get the this behavior as a [Member].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the member wasn't present.
     */
    public suspend fun asMember(): Member = supplier.getMember(guildId, id)

    /**
     * Requests to get this behavior as a [Member],
     * returns null if the member isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun asMemberOrNull(): Member? = supplier.getMemberOrNull(guildId, id)

    /**
     * Retrieve the [Member] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun fetchMember(): Member = supplier.getMember(guildId, id)


    /**
     * Retrieve the [Member] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun fetchMemberOrNull(): Member? = supplier.getMemberOrNull(guildId, id)

    /**
     * Requests to kick this member from its guild.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun kick(reason: String? = null): Unit = guild.kick(id, reason)

    /**
     * Requests to add the [Role] with the [roleId] to this member.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun addRole(roleId: Snowflake, reason: String? = null) {
        kord.rest.guild.addRoleToGuildMember(guildId = guildId, userId = id, roleId = roleId, reason = reason)
    }

    /**
     * Requests to get the [Guild] this member is part of.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] this member is part of,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to remove the [Role] with the [roleId] from this member.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun removeRole(roleId: Snowflake, reason: String? = null) {
        kord.rest.guild.deleteRoleFromGuildMember(guildId = guildId, userId = id, roleId = roleId, reason = reason)
    }

    /**
     * Requests to get the [Presence] of this member in the [guild].
     *
     * This property is not resolvable through REST and will always use the [KordCache] instead.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Presence] wasn't present.
     */
    public suspend fun getPresence(): Presence = getPresenceOrNull() ?: EntityNotFoundException.guildEntityNotFound(
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
    public suspend fun getPresenceOrNull(): Presence? {
        val data = kord.cache.query<PresenceData> {
            idEq(PresenceData::userId, id)
            idEq(PresenceData::guildId, guildId)
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
    public suspend fun getVoiceState(): VoiceState = getVoiceStateOrNull() ?: EntityNotFoundException.guildEntityNotFound(
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
    public suspend fun getVoiceStateOrNull(): VoiceState? {
        val data = kord.cache.query<VoiceStateData> {
            idEq(VoiceStateData::userId, id)
            idEq(VoiceStateData::guildId, guildId)
        }.singleOrNull() ?: return null

        return VoiceState(data, kord)
    }

    /**
     * Returns a new [MemberBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MemberBehavior =
        MemberBehavior(guildId = guildId, id = id, kord = kord, strategy = strategy)

}

public fun MemberBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): MemberBehavior = object : MemberBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is MemberBehavior -> other.id == id && other.guildId == guildId
        is UserBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "MemberBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to ban this member.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun MemberBehavior.ban(builder: BanCreateBuilder.() -> Unit = {}) {
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
public suspend inline fun MemberBehavior.edit(builder: MemberModifyBuilder.() -> Unit): Member {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.modifyGuildMember(guildId, id, builder)
    return Member(
        MemberData.from(userId = response.user.value!!.id, guildId = guildId, response),
        UserData.from(response.user.value!!),
        kord
    )
}
