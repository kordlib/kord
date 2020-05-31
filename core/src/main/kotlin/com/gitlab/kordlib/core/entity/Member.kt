package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.cache.data.MemberData
import com.gitlab.kordlib.core.cache.data.UserData
import com.gitlab.kordlib.core.toInstant
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * An instance of a [Discord Member](https://discordapp.com/developers/docs/resources/guild#guild-member-object).
 */
class Member(val memberData: MemberData, userData: UserData, kord: Kord, override val strategy: EntitySupplyStrategy = kord.resources.defaultStrategy
) : User(userData, kord, strategy), MemberBehavior {


    override val guildId: Snowflake
        get() = Snowflake(memberData.guildId)

    /**
     * The name as shown in the discord client, prioritizing the [nickname] over the [use].
     */
    val displayName: String get() = nickname ?: username

    /**
     * When the user joined this [guild].
     */
    val joinedAt: Instant get() = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(memberData.joinedAt, Instant::from)

    /**
     * The guild-specific nickname of the user, if present.
     */
    val nickname: String? get() = memberData.nick

    /**
     * When the user used their Nitro boost on the server.
     */
    val premiumSince: Instant? get() = memberData.premiumSince?.toInstant()

    /**
     * The ids of the [roles][Role] that apply to this user.
     */
    val roleIds: Set<Snowflake> get() = memberData.roles.asSequence().map { Snowflake(it) }.toSet()

    /**
     * The behaviors of the [roles][Role] that apply to this user.
     */
    val roleBehaviors: Set<RoleBehavior>
        get() = memberData.roles.asSequence().map { RoleBehavior(guildId = guildId, id = Snowflake(it), kord = kord) }.toSet()

    /**
     * The [roles][Role] that apply to this user.
     */
    val roles: Flow<Role> get() = roleIds.asFlow().map { strategy.supply(kord).getRoleOrNull(guildId, it) }.filterNotNull()

    /**
     * Whether this member's [id] equals the [Guild.ownerId]
     */
    suspend fun isOwner(): Boolean = getGuild().ownerId == id

    /**
     * Requests to calculate a summation of the permissions of this member's [roles].
     */
    suspend fun getPermissions(): Permissions {
        val guild = getGuild()
        val owner = guild.ownerId == this.id
        if (owner) return Permissions {
            +Permission.All
        }

        val everyone = guild.getEveryoneRole().permissions
        val roles = roles.map { it.permissions }.toList()

        return Permissions {
            +everyone
            roles.forEach { +it }
        }
    }

    /**
     * Returns this member.
     */
    override suspend fun asMember(): Member = this

    /**
     * Requests this user as a member of the guild, or returns itself when the [guildId] matches this member's [guild].
     * Returns null when the user is not a member of the guild.
     */
    override suspend fun asMember(guildId: Snowflake): Member = when (guildId) {
        this.guildId -> this
        else -> strategy.supply(kord).getMember(guildId, id)
    }


    override suspend fun asMemberOrNull(guildId: Snowflake): Member? = when (guildId) {
        this.guildId -> this
        else -> strategy.supply(kord).getMemberOrNull(guildId, id)
    }

    /**
     * returns a new [Member] with the given [strategy].
     *
     * @param strategy the strategy to use for the new instance. By default [EntitySupplyStrategy.CacheWithRestFallback].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy): Member = Member(memberData, data, kord, strategy)

}
