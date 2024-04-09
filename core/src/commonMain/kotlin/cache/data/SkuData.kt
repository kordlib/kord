package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordSku
import dev.kord.common.entity.SkuFlags
import dev.kord.common.entity.SkuType
import dev.kord.common.entity.Snowflake

public data class SkuData(
    val id: Snowflake,
    val applicationId: Snowflake,
    val type: SkuType,
    val name: String,
    val slug: String,
    val flags: SkuFlags,
) {
    public companion object {
        public fun from(entity: DiscordSku): SkuData = with (entity) {
            SkuData(id, applicationId, type, name, slug, flags)
        }
    }
}