package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class DiscordChannel(
        val id: String,
        val type: ChannelType,
        @SerialName("guild_id")
        val guildId: String? = null,
        val position: Int? = null,
        @SerialName("permission_overwrites")
        val permissionOverwrites: List<Overwrite>? = null,
        val name: String? = null,
        val topic: String? = null,
        val nsfw: Boolean? = null,
        @SerialName("last_message_id")
        val lastMessageId: String? = null,
        val bitrate: Int? = null,
        @SerialName("user_limit")
        val userLimit: Int? = null,
        @SerialName("rate_limit_per_user")
        val rateLimitPerUser: Int? = null,
        val recipients: List<DiscordUser>? = null,
        val icon: String? = null,
        @SerialName("owner_id")
        val ownerId: String? = null,
        @SerialName("application_id")
        val applicationId: String? = null,
        @SerialName("parent_id")
        val parentId: String? = null,
        @SerialName("last_pin_timestamp")
        val lastPinTimestamp: String? = null
)

@Serializable
data class Overwrite(
        val id: String,
        val type: String,
        val allow: Int,
        val deny: Int
)

@Serializable(with = ChannelType.ChannelTypeSerializer::class)
enum class ChannelType(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    GuildText(0),
    DM(1),
    GuildVoice(2),
    GroupDm(3),
    GuildCategory(4),
    GuildNews(5),
    GuildStore(6);

    @Serializer(forClass = ChannelType::class)
    companion object ChannelTypeSerializer : KSerializer<ChannelType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ChannelType {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: ChannelType) {
            encoder.encodeInt(obj.code)
        }
    }
}