package dev.kord.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The raw developer team data gotten from the API.
 */
@Serializable
public data class DiscordTeam(
    val icon: String?,
    val id: Snowflake,
    val members: List<DiscordTeamMember>,
    @SerialName("owner_user_id")
    val ownerUserId: Snowflake,
)

/**
 * The state of membership on a Discord developer team.
 */
@Serializable(with = TeamMembershipState.TeamMembershipStateSerializer::class)
public sealed class TeamMembershipState(public val value: Int) {
    /**
     * Unknown membership state.
     */
    public class Unknown(value: Int) : TeamMembershipState(value)

    /**
     * The user has been invited.
     */
    public object Invited : TeamMembershipState(1)

    /**
     * The user has accepted the invitation.
     */
    public object Accepted : TeamMembershipState(2)


    public companion object TeamMembershipStateSerializer : KSerializer<TeamMembershipState> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("membership_state", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TeamMembershipState = when (val value = decoder.decodeInt()) {
            1 -> Invited
            2 -> Accepted
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: TeamMembershipState) {
            encoder.encodeInt(value.value)
        }
    }
}

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
     * A collection of permissions granted to this member.
     * At the moment, this collection will only have one element: `*`, meaning the member has all permissions.
     * This is because right now there are no other permissions. Read mode [here](https://discord.com/developers/docs/topics/teams#data-models-team-members-object)
     */
    val permissions: List<String>,
    /**
     * The unique ID that this member belongs to.
     */
    @SerialName("team_id")
    val teamId: Snowflake,
    /**
     * Partial user data containing only the ID, username, discriminator and avatar.
     */
    val user: DiscordUser,
)
