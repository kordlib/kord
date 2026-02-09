package dev.kord.core.entity

import dev.kord.common.annotation.DiscordAPIPreview
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.GuildTraitData

@DiscordAPIPreview
public class GuildTrait(
    public val data: GuildTraitData,
    override val kord: Kord,
): KordObject {
    /**
     * The ID of the emoji associated with this trait
     */
    public val emojiId: Snowflake? get() = data.emojiId

    /**
     * The name of hte emoji associated with this trait
     */
    public val emojiName: String? get() = data.emojiName

    /**
     * Whether the emoji is animated
     */
    public val emojiAnimated: Boolean get() = data.emojiAnimated

    /**
     * The name of the trait
     */
    public val label: String get() = data.label

    /**
     * The position of the trait in the array
     */
    public val position: Int get() = data.position
}