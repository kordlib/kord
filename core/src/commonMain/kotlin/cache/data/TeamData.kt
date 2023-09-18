package dev.kord.core.cache.data

import dev.kord.common.entity.*
import kotlinx.serialization.Serializable

/**
 * A serializable data representation of a Discord developer team.
 */
@Serializable
public data class TeamData(
    val id: Snowflake,
    val icon: String? = null,
    val members: List<TeamMemberData>,
    val ownerUserId: Snowflake,
    val name: String,
) {
    public companion object {
        public fun from(entity: DiscordTeam): TeamData = with(entity) {
            TeamData(id, icon, members.map { TeamMemberData.from(it) }, ownerUserId, name)
        }
    }
}

/**
 * A serializable data representation of a Discord developer team member.
 */
@Serializable
public data class TeamMemberData(
    public val membershipState: TeamMembershipState,
    public val teamId: Snowflake,
    public val userId: Snowflake,
    public val role: TeamMemberRole,
) {
    public companion object {
        public fun from(entity: DiscordTeamMember): TeamMemberData = with(entity) {
            TeamMemberData(membershipState, teamId = teamId, userId = user.id, role)
        }
    }
}
