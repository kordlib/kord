package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordPrimaryGuild
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
public data class PrimaryGuildData(
    val identityGuildId: Snowflake?,
    val identityEnabled: Boolean?,
    val tag: String?,
    val badge: String?
) {
    public companion object {
        public fun from(entity: DiscordPrimaryGuild): PrimaryGuildData = with(entity) {
            PrimaryGuildData(identityGuildId, identityEnabled, tag, badge)
        }
    }
}

public fun DiscordPrimaryGuild.toData(): PrimaryGuildData = PrimaryGuildData.from(this)