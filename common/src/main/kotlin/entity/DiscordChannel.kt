package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A representation of a [Discord Channel Structure](https://discord.com/developers/docs/resources/channel).
 *
 * @param id The id of the channel.
 * @param type the Type of channel.
 * @param guildId the id of the guild.
 * @param position The sorting position of the channel.
 * @param permissionOverwrites The explicit permission overwrite for members and roles.
 * @param name The name of the channel.
 * @param topic The channel topic.
 * @param nsfw Whether the channel is nsfw.
 * @param lastMessageId The id of the last message sent in this channel (may not point to an existing or valid message).
 * @param bitrate The bitrate (in bits) of the voice channel.
 * @param userLimit The user limit of the voice channel.
 * @param rateLimitPerUser amount of seconds a user has to wait before sending another message; bots,
 * as well as users with the permission [Permission.ManageMessages] or [Permission.ManageChannels] are unaffected.
 * @param recipients The recipients of the DM.
 * @param icon The icon hash.
 * @param ownerId The id of DM creator.
 * @param applicationId The application id of the group DM creator if it is bot-created.
 * @param parentId The id of the parent category for a channel.
 * @param lastPinTimestamp When the last pinned message was pinned.
 */
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
    val permissions: Optional<Permissions> = Optional.Missing(),
    @SerialName("message_count")
    val messageCount: OptionalInt = OptionalInt.Missing,
    @SerialName("member_count")
    val memberCount: OptionalInt = OptionalInt.Missing,
    val threadMetadata: Optional<DiscordThreadMetadata> = Optional.Missing(),
    val member: Optional<DiscordThreadMember> = Optional.Missing()
)

@Serializable(with = ChannelType.Serializer::class)
sealed class ChannelType(val value: Int) {
    /** The default code for unknown values. */
    class Unknown(value: Int) : ChannelType(value)

    /** A text channel within a server. */
    object GuildText : ChannelType(0)

    /** A direct message between users. */
    object DM : ChannelType(1)

    /** A voice channel within a server. */
    object GuildVoice : ChannelType(2)

    /** A direct message between multiple users. */
    object GroupDM : ChannelType(3)

    /** An organization category. */
    object GuildCategory : ChannelType(4)

    /** A channel that users can follow and crosspost into their own server. */
    object GuildNews : ChannelType(5)

    /** A channel in which game developers can sell their game on Discord. */
    object GuildStore : ChannelType(6)

    object NewsThread : ChannelType(10)

    object PrivateThread : ChannelType(11)

    object PublicThread : ChannelType(12)

    object GuildStageVoice : ChannelType(13)

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
            6 -> GuildStore
            10 -> NewsThread
            11 -> PrivateThread
            12 -> PublicThread
            13 -> GuildStageVoice
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

@Serializable
class DiscordThreadMetadata(
    val archived: Boolean,
    @SerialName("archiver_id")
    val archiverId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("archive_timestamp")
    val archiveTimestamp: String,
    val autoArchiveDuration: Int,
    val locked: OptionalBoolean = OptionalBoolean.Missing
)