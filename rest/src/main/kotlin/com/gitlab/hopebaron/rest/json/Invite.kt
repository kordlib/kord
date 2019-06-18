package com.gitlab.hopebaron.rest.json

import com.gitlab.hopebaron.common.entity.Channel
import com.gitlab.hopebaron.common.entity.PartialGuild
import com.gitlab.hopebaron.common.entity.User
import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class Invite(
        val code: String,
        val guild: PartialGuild?,
        val channel: Channel? = null,
        @SerialName("target_user")
        val targetUser: User,
        @SerialName("target_user_type")
        val targetUserType: TargetUserType? = null,
        @SerialName("approximate_presence_count")
        val approximatePresenceCount: Int? = null,
        @SerialName("approximate_member_count")
        val approximateMemberCount: Int? = null
)

@Serializable(with = TargetUserType.TargetUserTypeSerializer::class)
enum class TargetUserType(val code: Int) {
    STREAM(1);

    @Serializer(forClass = TargetUserType::class)
    companion object TargetUserTypeSerializer : KSerializer<TargetUserType> {
        override val descriptor: SerialDescriptor = IntDescriptor.withName("TargetUserType")

        override fun deserialize(decoder: Decoder): TargetUserType {
            val code = decoder.decodeInt()

            return values().first { it.code == code }
        }

        override fun serialize(encoder: Encoder, obj: TargetUserType) {
            encoder.encodeInt(obj.code)
        }
    }

}

@Serializable
data class InviteMetaData(
        val inviter: User,
        val uses: Int,
        @SerialName("max_uses")
        val maxUses: Int,
        @SerialName("max_age")
        val maxAge: Int,
        val temporary: Boolean,
        @SerialName("created_at")
        val createdAt: String,
        val revoked: Boolean
)