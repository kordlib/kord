package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.PrimaryGuildData

public class PrimaryGuild(
    public val data: PrimaryGuildData,
    override val kord: Kord
) : KordObject {
    public val identityGuildId: Snowflake? get() = data.identityGuildId

    public val identityEnabled: Boolean? get() = data.identityEnabled

    public val tag: String? get() = data.tag

    public val badgeHash: String? = data.badge

    public val badge: Asset? = badgeHash?.let { identityGuildId?.let { guildId -> Asset.tagBadge(guildId, it, kord) } }
}