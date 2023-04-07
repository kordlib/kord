package dev.kord.core.cache.data

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordPartialChannel
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.map
import kotlinx.serialization.Serializable

@Serializable
public class PartialChannelData(
    public val id: Snowflake,
    public val type: ChannelType,
    public val name: Optional<String?> = Optional.Missing(),
    public val parentId: OptionalSnowflake? = OptionalSnowflake.Missing,
    public val permissions: Optional<Permissions> = Optional.Missing(),
    public val threadMetadata: Optional<ThreadMetadataData> = Optional.Missing()
) {
    public companion object {
        public fun from(partialChannel: DiscordPartialChannel): PartialChannelData = with(partialChannel) {
            PartialChannelData(
                id,
                type,
                name,
                parentId,
                permissions,
                threadMetadata.map { ThreadMetadataData.from(it) }
            )
        }
    }
}