package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import kotlinx.serialization.Serializable

@Serializable
public data class ChannelData(
    val id: Snowflake,
    val type: ChannelType,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val position: OptionalInt = OptionalInt.Missing,
    val permissionOverwrites: Optional<List<Overwrite>> = Optional.Missing(),
    val name: Optional<String> = Optional.Missing(),
    val topic: Optional<String?> = Optional.Missing(),
    val nsfw: OptionalBoolean = OptionalBoolean.Missing,
    val lastMessageId: OptionalSnowflake? = OptionalSnowflake.Missing,
    val bitrate: OptionalInt = OptionalInt.Missing,
    val userLimit: OptionalInt = OptionalInt.Missing,
    val rateLimitPerUser: OptionalInt = OptionalInt.Missing,
    val recipients: Optional<List<Snowflake>> = Optional.Missing(),
    val icon: Optional<String?> = Optional.Missing(),
    val ownerId: OptionalSnowflake = OptionalSnowflake.Missing,
    val applicationId: OptionalSnowflake = OptionalSnowflake.Missing,
    val parentId: OptionalSnowflake? = OptionalSnowflake.Missing,
    val lastPinTimestamp: Optional<String?> = Optional.Missing(),
    val rtcRegion: Optional<String?> = Optional.Missing(),
    val permissions: Optional<Permissions> = Optional.Missing(),
    val threadMetadata: Optional<ThreadMetadataData> = Optional.Missing(),
    val messageCount: OptionalInt = OptionalInt.Missing,
    val memberCount: OptionalInt = OptionalInt.Missing,
    val defaultAutoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing(),
    val member: Optional<ThreadMemberData> = Optional.Missing()
) {


    public companion object {
        public val description: DataDescription<ChannelData, Snowflake> = description(ChannelData::id)

        public fun from(entity: DiscordChannel): ChannelData = with(entity) {
            ChannelData(
                id,
                type,
                guildId,
                position,
                permissionOverwrites,
                name,
                topic,
                nsfw,
                lastMessageId,
                bitrate,
                userLimit,
                rateLimitPerUser,
                recipients.mapList { it.id },
                icon,
                ownerId,
                applicationId,
                parentId,
                lastPinTimestamp,
                rtcRegion,
                permissions,
                threadMetadata.map { ThreadMetadataData.from(it) },
                messageCount,
                memberCount,
                defaultAutoArchiveDuration,
                member.map { ThreadMemberData.from(it, id) }
            )
        }
    }

}

@Serializable
public data class ThreadMetadataData(
    val archived: Boolean,
    val archiveTimestamp: String,
    val autoArchiveDuration: ArchiveDuration,
    val locked: OptionalBoolean = OptionalBoolean.Missing,
    val invitable: OptionalBoolean = OptionalBoolean.Missing
) {
    
  public companion object {
        public fun from(threadMetadata: DiscordThreadMetadata): ThreadMetadataData = with(threadMetadata) {
            ThreadMetadataData(archived, archiveTimestamp, autoArchiveDuration, locked, invitable)

        }
    }
}


public fun DiscordChannel.toData(): ChannelData = ChannelData.from(this)
