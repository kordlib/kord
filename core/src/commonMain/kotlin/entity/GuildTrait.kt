package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.GuildTraitData

public class GuildTrait(
    public val data: GuildTraitData,
    override val kord: Kord,
): KordObject {
    public val emojiId: Snowflake? get() = data.emojiId

    public val emojiName: String? get() = data.emojiName

    public val emojiAnimated: Boolean get() = data.emojiAnimated

    public val label: String get() = data.label

    public val position: Int get() = data.position
}