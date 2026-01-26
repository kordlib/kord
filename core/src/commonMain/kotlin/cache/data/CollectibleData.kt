package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordCollectible
import dev.kord.common.entity.DiscordNameplate
import dev.kord.common.entity.optional.Optional

public data class CollectibleData(
    val nameplate: Optional<DiscordNameplate> = Optional.Missing()
) {
    public companion object {
        public fun from(entity: DiscordCollectible): CollectibleData = with(entity) {
            CollectibleData(nameplate)
        }
    }
}

public fun DiscordCollectible.toData(): CollectibleData = CollectibleData.from(this)