package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

@Serializable
@KordUnstableApi
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
@KordUnstableApi
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
@KordUnstableApi
data class DiscordClientStatus(val desktop: Status? = null, val mobile: Status? = null, val web: Status? = null)

@Serializable(with = Status.StatusSerializer::class)
enum class Status {
        /** The default code for unknown values. */
        Unknown,
        Online, DnD, Idle, Invisible, Offline;

        @Serializer(forClass = Status::class)
        companion object StatusSerializer : KSerializer<Status> {
                override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Status", PrimitiveKind.STRING)

                override fun deserialize(decoder: Decoder): Status {
                        val name = decoder.decodeString()
                        return values().firstOrNull { it.name.toLowerCase() == name } ?: Unknown
                }

                override fun serialize(encoder: Encoder, value: Status) {
                        encoder.encodeString(value.name.toLowerCase())
                }
        }
}