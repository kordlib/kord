package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.JsonElement

@Serializable
data class DiscordPresenceUpdateData(
        val user: DiscordPresenceUser,
        val roles: List<String>? = null,
        val game: DiscordActivity? = null,
        @SerialName("guild_id")
        val guildId: String? = null, //don't trust the docs
        val status: Status,
        val activities: List<DiscordActivity>,
        @SerialName("client_status")
        val clientStatus: DiscordClientStatus
)

@Serializable
data class DiscordPresenceUser(
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
data class DiscordClientStatus(val desktop: Status? = null, val mobile: Status? = null, val web: Status? = null)

@Serializable(with = Status.StatusSerializer::class)
enum class Status {
        /** The default code for unknown values. */
        Unknown,
        Online, DnD, Idle, Invisible, Offline;

        @Serializer(forClass = Status::class)
        companion object StatusSerializer : KSerializer<Status> {
                override val descriptor: SerialDescriptor = StringDescriptor.withName("Status")

                override fun deserialize(decoder: Decoder): Status {
                        val name = decoder.decodeString()
                        return values().firstOrNull { it.name.toLowerCase() == name } ?: Unknown
                }

                override fun serialize(encoder: Encoder, obj: Status) {
                        encoder.encodeString(obj.name.toLowerCase())
                }
        }
}