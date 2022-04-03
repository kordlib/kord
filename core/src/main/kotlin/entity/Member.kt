package dev.kord.core.entity

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.UserData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import java.util.*

/**
 * An instance of a [Discord Member](https://discord.com/developers/docs/resources/guild#guild-member-object).
 */
public class Member(
    public val memberData: MemberData,
    userData: UserData,
    kord: Kord,
    supplier: EntitySupplier = kord.defaultSupplier
) : User(userData, kord, supplier), MemberBehavior {

    override val guildId: Snowflake
        get() = memberData.guildId

    /**
     * The name as shown in the discord client, prioritizing the [nickname] over the [use].
     */
    public val displayName: String get() = nickname ?: username

    /**
     * The members guild avatar as [Icon] object
     */
    public val memberAvatar: Icon?
        get() = memberData.avatar.value?.let { Icon.MemberAvatar(memberData.guildId, data.id, it, kord) }

    /**
     * When the user joined this [guild].
     */
    public val joinedAt: Instant get() = memberData.joinedAt

    /**
     * The guild-specific nickname of the user, if present.
     */
    public val nickname: String? get() = memberData.nick.value

    /**
     * When the user used their Nitro boost on the server.
     */
    public val premiumSince: Instant? get() = memberData.premiumSince.value

    /**
     * The ids of the [roles][Role] that apply to this user.
     */
    public val roleIds: Set<Snowflake> get() = memberData.roles.toSet()

    /**
     * The behaviors of the [roles][Role] that apply to this user.
     */
    public val roleBehaviors: Set<RoleBehavior>
        get() = roleIds.map { RoleBehavior(guildId = guildId, id = it, kord = kord) }.toSet()

    /**
     * The [roles][Role] that apply to this user.
     *
     * This request uses state [data] to resolve the entities belonging to the flow,
     * as such it can't guarantee an up to date representation if the [data] is outdated.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val roles: Flow<Role>
        get() = if (roleIds.isEmpty()) emptyFlow()
        else supplier.getGuildRoles(guildId).filter { it.id in roleIds }

    /**
     * Whether the user has not yet passed the guild's Membership Screening requirements.
     */
    public val isPending: Boolean get() = memberData.pending.discordBoolean

    /**
     * The [Instant] until the user's timeout expires, or `null` if the user does not have a timeout.
     */
    public val communicationDisabledUntil: Instant? get() = memberData.communicationDisabledUntil.value

    /**
     * Whether this member's [id] equals the [Guild.ownerId].
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun isOwner(): Boolean = getGuild().ownerId == id

    /**
     * Requests to calculate a summation of the permissions of this member's [roles].
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getPermissions(): Permissions {
        val guild = getGuild()
        val owner = guild.ownerId == this.id
        if (owner) return Permissions(Permission.All)

        val everyone = guild.getEveryoneRole().permissions
        val roles = roles.map { it.permissions }.toList()

        return Permissions {
            +everyone
            roles.forEach { +it }
        }
    }

    /**
     * Returns a new [Member] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Member =
        Member(memberData, data, kord, strategy.supply(kord))

    override suspend fun asUser(): User = this

    override suspend fun asUserOrNull(): User = this

    override suspend fun asMember(guildId: Snowflake): Member = this

    override suspend fun asMember(): Member = this

    override suspend fun asMemberOrNull(guildId: Snowflake): Member = this

    override suspend fun asMemberOrNull(): Member = this


    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is MemberBehavior -> other.id == id && other.guildId == guildId
        is UserBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Member(memberData=$memberData)"
    }

}
