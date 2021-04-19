package dev.kord.core.cache.data


import dev.kord.common.entity.DiscordTeam
import dev.kord.common.entity.DiscordTeamMember
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TeamMembershipState
import kotlinx.serialization.Serializable

/**
 * A serializable data representation of a Discord developer team.
 */
@Serializable
data class TeamData(
    val id: Snowflake,
    val icon: String? = null,
    val members: List<TeamMemberData>,
    val ownerUserId: Snowflake,
) {
    companion object {
        fun from(entity: DiscordTeam): TeamData = with(entity) {
            TeamData(id, icon, members.map { TeamMemberData.from(it) }, ownerUserId)
        }
    }
}

/**
 * A serializable data representation of a Discord developer team member.
 */
@Serializable
class TeamMemberData(
    val membershipState: TeamMembershipState,
    val permissions: List<String>,
    val teamId: Snowflake,
    val userId: Snowflake,
) {
    companion object {
        fun from(entity: DiscordTeamMember): TeamMemberData = with(entity) {
            TeamMemberData(membershipState, permissions, teamId = teamId, userId = user.id)
        }
    }
}
