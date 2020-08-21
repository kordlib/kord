package com.gitlab.kordlib.core.cache.data


import com.gitlab.kordlib.common.entity.TeamMembershipState
import kotlinx.serialization.Serializable

/**
 * A serializable data representation of a Discord developer team.
 */
@Serializable
data class TeamData(
        /**
         * The unique ID of this team.
         */
        val id: Long,
        /**
         * The hash of this team's icon.
         */
        val icon: String?,
        /**
         * A collection of all members of this team.
         */
        val members: List<TeamMemberData>,
        /**
         * The ID of the user that owns the team.
         */
        val ownerUserId: Long
)

/**
 * A serializable data representation of a Discord developer team member.
 */
@Serializable
class TeamMemberData(
        /**
         * An enumeration representing the membership state of this user.
         */
        val membershipState: TeamMembershipState,
        /**
         * A collection of permissions granted to this member.
         * At the moment, this collection will only have one element: `*`, meaning the member has all permissions.
         * This is because right now there are no other permissions. Read mode [here](https://discord.com/developers/docs/topics/teams#data-models-team-members-object)
         */
        val permissions: List<String>,
        /**
         * The unique ID that this member belongs to.
         */
        val teamId: Long,
        /**
         * The ID of the user this member represents.
         */
        val userId: Long
)
