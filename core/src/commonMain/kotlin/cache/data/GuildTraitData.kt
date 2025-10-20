package dev.kord.core.cache.data

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordGuildTrait
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@KordPreview
@Serializable
public data class GuildTraitData(
    val emojiId: Snowflake? = null,
    val emojiName: String? = null,
    val emojiAnimated: Boolean,
    val label: String,
    val position: Int
) {
    public companion object {
        public fun from(entity: DiscordGuildTrait): GuildTraitData = with(entity) {
            GuildTraitData(
                emojiId = emojiId,
                emojiName = emojiName,
                emojiAnimated = emojiAnimated,
                label = label,
                position = position
            )
        }
    }
}

@KordPreview
public fun DiscordGuildTrait.toData(): GuildTraitData = GuildTraitData.from(this)