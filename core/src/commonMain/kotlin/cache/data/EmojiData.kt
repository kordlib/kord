package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.*
import kotlinx.serialization.Serializable

@Serializable
public data class EmojiData(
    val id: Snowflake,
    val guildId: Snowflake,
    val name: String? = null,
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    val roles: Optional<List<Snowflake>> = Optional.Missing(),
    val requireColons: OptionalBoolean = OptionalBoolean.Missing,
    val managed: OptionalBoolean = OptionalBoolean.Missing,
    val animated: OptionalBoolean = OptionalBoolean.Missing,
    val available: OptionalBoolean = OptionalBoolean.Missing
) {
    public companion object {
        public val description: DataDescription<EmojiData, Snowflake> = description(EmojiData::id)

        public fun from(guildId: Snowflake, id: Snowflake, entity: DiscordEmoji): EmojiData =
            with(entity) {
                EmojiData(
                    id,
                    guildId,
                    name,
                    user.value?.id.optionalSnowflake() ?: OptionalSnowflake.Missing,
                    roles,
                    requireColons,
                    managed,
                    animated,
                    available,
                )
            }
    }
}

public fun DiscordEmoji.toData(guildId: Snowflake, id: Snowflake): EmojiData {
    return EmojiData.from(guildId, id, this)
}
