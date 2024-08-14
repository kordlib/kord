@file:Generate(
    STRING_KORD_ENUM, name = "TeamMemberRole",
    docUrl = "https://discord.com/developers/docs/topics/teams#team-member-roles",
    entries = [
        Entry(
            "Admin", stringValue = "admin",
            kDoc = "Admins have similar access as owners, except they cannot take destructive actions on the team or " +
                "team-owned apps.",
        ),
        Entry(
            "Developer", stringValue = "developer",
            kDoc = "Developers can access information about team-owned apps, like the client secret or public key. " +
                "They can also take limited actions on team-owned apps, like configuring interaction endpoints or " +
                "resetting the bot token. Members with the Developer role _cannot_ manage the team or its members, " +
                "or take destructive actions on team-owned apps.",
        ),
        Entry(
            "ReadOnly", stringValue = "read_only",
            kDoc = "Read-only members can access information about a team and any team-owned apps. Some examples " +
                "include getting the IDs of applications and exporting payout records.",
        ),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "TeamMembershipState",
    docUrl = "https://discord.com/developers/docs/topics/teams#data-models-membership-state-enum",
    entries = [
        Entry("Invited", intValue = 1),
        Entry("Accepted", intValue = 2),
    ],
)

package dev.kord.common.entity

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.EntityType.STRING_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The raw developer team data gotten from the API.
 */
@Serializable
public data class DiscordTeam(
    val icon: String?,
    val id: Snowflake,
    val members: List<DiscordTeamMember>,
    val name: String,
    @SerialName("owner_user_id")
    val ownerUserId: Snowflake,
)

/**
 * The raw developer team member data gotten from the API.
 */
@Serializable
public data class DiscordTeamMember(
    /**
     * An integer enum representing the state of membership of this user.
     * `1` means the user has been invited and `2` means the user has accepted the invitation.
     */
    @SerialName("membership_state")
    val membershipState: TeamMembershipState,
    /**
     * The unique ID that this member belongs to.
     */
    @SerialName("team_id")
    val teamId: Snowflake,
    /**
     * Partial user data containing only the ID, username, discriminator and avatar.
     */
    val user: DiscordUser,
    val role: TeamMemberRole,
)
