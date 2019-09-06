package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializable
data class PresenceUpdateData(
        val user: PresenceUser,
        val roles: List<String>? = null,
        val game: Activity? = null,
        @SerialName("guild_id")
        val guildId: String? = null,
        val status: Status,
        val activities: List<Activity>,
        @SerialName("client_status")
        val clientStatus: ClientStatus
)

@Serializable
data class PresenceUser(
        val id: String,
        val username: String? = null,
        val discriminator: String? = null,
        val avatar: String? = null,
        val bot: String? = null,
        @SerialName("mfa_enable")
        val mfaEnable: String? = null,
        val locale: String? = null,
        val flags: String? = null,
        @SerialName("premium_type")
        val premiumType: Premium? = null,
        val verified: String? = null,
        val email: String? = null
)

@Serializable
data class ClientStatus(val desktop: Status? = null, val mobile: Status? = null, val web: Status? = null)

@Serializable(with = Status.StatusSerializer::class)
enum class Status {
        Online, DnD, Idle, Invisible, Offline;

        @Serializer(forClass = Status::class)
        companion object StatusSerializer : KSerializer<Status> {
                override val descriptor: SerialDescriptor = StringDescriptor.withName("Status")

                override fun deserialize(decoder: Decoder): Status {
                        val name = decoder.decodeString()
                        return values().first { it.name.toLowerCase() == name }
                }

                override fun serialize(encoder: Encoder, obj: Status) {
                        encoder.encodeString(obj.name.toLowerCase())
                }
        }
}