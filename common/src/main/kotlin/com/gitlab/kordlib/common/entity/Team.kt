package com.gitlab.kordlib.common.entity

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
data class DiscordTeam(
        /**
         * The unique ID of this team.
         */
        val id: String,
        /**
         * The hash of this team's icon.
         */
        val icon: String?,
        /**
         * A collection of all members of this team.
         */
        val members: List<DiscordTeamMember>,
        /**
         * The ID of the user that owns the team.
         */
        @SerialName("owner_user_id")
        val ownerUserId: String
)

/**
 * The state of membership on a Discord developer team.
 */
@Serializable(with = TeamMembershipState.TeamMembershipStateSerializer::class)
enum class TeamMembershipState(val value: Int) {
    /**
     * Unknown membership state.
     */
    Unknown(-1),

    /**
     * The user has been invited.
     */
    Invited(1),

    /**
     * The user has accepted the invitation.
     */
    Accepted(2);

    @OptIn(ExperimentalSerializationApi::class)
    @Serializer(forClass = TeamMembershipState::class)
    companion object TeamMembershipStateSerializer : KSerializer<TeamMembershipState> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("membership_state", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TeamMembershipState {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.value == code } ?: Unknown
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
class DiscordTeamMember(
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
        val teamId: String,
        /**
         * Partial user data containing only the ID, username, discriminator and avatar.
         */
        val user: DiscordUser
)
