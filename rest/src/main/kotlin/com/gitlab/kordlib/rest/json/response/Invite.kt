package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.entity.Channel
import com.gitlab.kordlib.common.entity.PartialGuild
import com.gitlab.kordlib.common.entity.User
import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class InviteResponse(
        val code: String? = null,
        val guild: PartialGuild? = null,
        val channel: Channel? = null,
        @SerialName("target_user")
        val targetUser: User? = null,
        @SerialName("target_user_type")
        val targetUserType: TargetUserTypeResponse? = null,
        @SerialName("approximate_presence_count")
        val approximatePresenceCount: Int? = null,
        @SerialName("approximate_member_count")
        val approximateMemberCount: Int? = null
)

@Serializable(with = TargetUserTypeResponse.TargetUserTypeSerializer::class)
enum class TargetUserTypeResponse(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    STREAM(1);

    @Serializer(forClass = TargetUserTypeResponse::class)
    companion object TargetUserTypeSerializer : KSerializer<TargetUserTypeResponse> {
        override val descriptor: SerialDescriptor = IntDescriptor.withName("TargetUserType")

        override fun deserialize(decoder: Decoder): TargetUserTypeResponse {
            val code = decoder.decodeInt()

            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: TargetUserTypeResponse) {
            encoder.encodeInt(obj.code)
        }
    }

}

@Serializable
data class InviteMetaDataResponse(
        val inviter: User,
        val uses: Int,
        @SerialName("max_uses")
        val maxUses: Int,
        @SerialName("max_age")
        val maxAge: Int,
        val temporary: Boolean,
        @SerialName("created_at")
        val createdAt: String
)

@Serializable
data class PartialInvite(val code: String? = null)