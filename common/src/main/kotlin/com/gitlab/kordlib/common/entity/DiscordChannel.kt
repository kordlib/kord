package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalInt
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class DiscordChannel(
        val id: Snowflake,
        val type: ChannelType,
        @SerialName("guild_id")
        val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
        val position: OptionalInt = OptionalInt.Missing,
        @SerialName("permission_overwrites")
        val permissionOverwrites: Optional<List<Overwrite>> = Optional.Missing(),
        val name: Optional<String> = Optional.Missing(),
        val topic: Optional<String?> = Optional.Missing(),
        val nsfw: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("last_message_id")
        val lastMessageId: OptionalSnowflake? = OptionalSnowflake.Missing,
        val bitrate: OptionalInt = OptionalInt.Missing,
        @SerialName("user_limit")
        val userLimit: OptionalInt = OptionalInt.Missing,
        @SerialName("rate_limit_per_user")
        val rateLimitPerUser: OptionalInt = OptionalInt.Missing,
        val recipients: Optional<List<DiscordUser>> = Optional.Missing(),
        val icon: Optional<String?> = Optional.Missing(),
        @SerialName("owner_id")
        val ownerId: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("application_id")
        val applicationId: OptionalSnowflake = OptionalSnowflake.Missing,
        @SerialName("parent_id")
        val parentId: OptionalSnowflake? = OptionalSnowflake.Missing,
        @SerialName("last_pin_timestamp")
        val lastPinTimestamp: Optional<String?> = Optional.Missing(),
)

@Serializable(with = ChannelType.Serializer::class)
sealed class ChannelType(val value: Int) {
    /** The default code for unknown values. */
    class Unknown(value: Int) : ChannelType(value)
    object GuildText : ChannelType(0)
    object DM : ChannelType(1)
    object GuildVoice : ChannelType(2)
    object GroupDM : ChannelType(3)
    object GuildCategory : ChannelType(4)
    object GuildNews : ChannelType(5)
    object GuildStore : ChannelType(6)

    companion object;

    internal object Serializer : KSerializer<ChannelType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ChannelType = when (val code = decoder.decodeInt()) {
            0 -> GuildText
            1 -> DM
            2 -> GuildVoice
            3 -> GroupDM
            4 -> GuildCategory
            5 -> GuildNews
            96 -> GuildStore
            else -> Unknown(code)
        }

        override fun serialize(encoder: Encoder, value: ChannelType) = encoder.encodeInt(value.value)
    }

}

@Serializable
data class Overwrite(
        val id: Snowflake,
        val type: OverwriteType,
        val allow: Permissions,
        val deny: Permissions,
) {
    companion object;

}

@Serializable(with = OverwriteType.Serializer::class)
sealed class OverwriteType(val value: Int) {

    class Unknown(value: Int) : OverwriteType(value)
    object Role : OverwriteType(0)
    object Member : OverwriteType(1)

    companion object;

    internal object Serializer : KSerializer<OverwriteType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.Overwrite.Type", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): OverwriteType = when (val value = decoder.decodeInt()) {
            0 -> Role
            1 -> Member
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: OverwriteType) {
            encoder.encodeInt(value.value)
        }
    }
}
