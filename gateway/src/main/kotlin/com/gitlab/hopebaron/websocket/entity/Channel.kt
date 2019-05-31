package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class Channel(
        val id: Snowflake,
        val type: ChannelType,
        @SerialName("guild_id")
        val guildId: Snowflake? = null,
        val position: Int? = null,
        @SerialName("permission_overwrites")
        val permissionOverwrites: List<Overwrite>? = null,
        val name: String? = null,
        val topic: String? = null,
        val nsfw: Boolean? = null,
        @SerialName("last_message_id")
        val lastMessageId: Snowflake? = null,
        val bitrate: Int? = null,
        @SerialName("user_limit")
        val userLimit: Int? = null,
        @SerialName("rate_limit_per_user")
        val rateLimitPerUser: Int? = null,
        val recipients: List<User>? = null,
        val icon: String? = null,
        @SerialName("owner_id")
        val ownerId: Snowflake? = null,
        @SerialName("application_id")
        val applicationId: Snowflake? = null,
        @SerialName("parent_id")
        val parentId: Snowflake? = null,
        @SerialName("last_pin_timestamp")
        val lastPinTimestamp: String? = null
)

@Serializable(with = ChannelType.ChannelTypeSerializer::class)
enum class ChannelType(val code: Int) {
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
            get() = IntDescriptor.withName("type")

        override fun deserialize(decoder: Decoder): ChannelType {
            val code = decoder.decodeInt()
            return values().first { it.code == code }
        }

        override fun serialize(encoder: Encoder, obj: ChannelType) {
            encoder.encodeInt(obj.code)
        }
    }
}