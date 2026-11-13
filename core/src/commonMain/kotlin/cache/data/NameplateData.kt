package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordNameplate
import dev.kord.common.entity.NameplatePalette
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class NameplateData(
    val skuId: Snowflake,
    val asset: String,
    val label: String,
    val palette: NameplatePalette
) {
    public companion object {
        public fun from(entity: DiscordNameplate): NameplateData = with(entity) {
            NameplateData(skuId, asset, label, palette)
        }
    }
}

public fun DiscordNameplate.toData(): NameplateData = NameplateData.from(this)