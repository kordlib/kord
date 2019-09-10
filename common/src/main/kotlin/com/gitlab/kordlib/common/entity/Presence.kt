package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.JsonElement

@Serializable
data class PresenceUpdateData(
        val user: PresenceUser,
        val roles: List<String>,
        val game: Activity? = null,
        @SerialName("guild_id")
        val guildId: String,
        val status: Status,
        val activities: List<Activity>,
        @SerialName("client_status")
        val clientStatus: ClientStatus
)

@Serializable
data class PresenceUser(
        val id: String,
        val username: JsonElement? = null,
        val discriminator: JsonElement? = null,
        val avatar: JsonElement? = null,
        val bot: JsonElement? = null,
        @SerialName("mfa_enable")
        val mfaEnable: JsonElement? = null,
        val locale: JsonElement? = null,
        val flags: JsonElement? = null,
        @SerialName("premium_type")
        val premiumType: JsonElement? = null,
        val verified: JsonElement? = null,
        val email: JsonElement? = null
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