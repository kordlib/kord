package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TeamMemberRole
import dev.kord.common.entity.TeamMembershipState
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.TeamData
import dev.kord.core.cache.data.TeamMemberData
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A Discord [developer team](https://discord.com/developers/docs/topics/teams) which can own applications.
 */
public class Team(
    public val data: TeamData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordEntity, Strategizable {
    override val id: Snowflake get() = data.id

    /** The hash of this team's icon. */
    public val iconHash: String? get() = data.icon

    /** This team's icon. */
    public val icon: Asset? get() = iconHash?.let { Asset.teamIcon(id, it, kord) }

    /**
     * A collection of all members of this team.
     */
    public val members: List<TeamMember>
        get() = data.members.map { TeamMember(it, kord) }

    /** Name of the team. */
    public val name: String get() = data.name

    /**
     * The ID of the user that owns the team.
     */
    public val ownerUserId: Snowflake
        get() = data.ownerUserId


    /**
     * Requests to get the team owner through the [supplier].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [User] wasn't present.
     */
    public suspend fun getUser(): User = supplier.getUser(ownerUserId)

    /**
     * Requests to get the team owner through the [supplier],
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getUserOrNUll(): User? = supplier.getUserOrNull(ownerUserId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Team = Team(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "Team(data=$data, kord=$kord, supplier=$supplier)"
    }

}

/**
 * A member of a Discord developer team.
 */
public class TeamMember(public val data: TeamMemberData, public val kord: Kord) {
    /**
     * An enumeration representing the membership state of this user.
     */
    public val membershipState: TeamMembershipState get() = data.membershipState

    /**
     * The unique ID that this member belongs to.
     */
    public val teamId: Snowflake get() = data.teamId

    /**
     * The ID of the user this member represents.
     */
    public val userId: Snowflake get() = data.userId

    /**
     * Utility method that gets the user from Kord.
     */
    public suspend fun getUser(): User? = kord.getUser(userId)

    /** [Role][TeamMemberRole] of the team member. */
    public val role: TeamMemberRole get() = data.role

    override fun toString(): String {
        return "TeamMember(data=$data, kord=$kord)"
    }

}
