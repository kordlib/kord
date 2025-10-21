package dev.kord.core.cache.data

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import kotlinx.serialization.Serializable

@KordPreview
@Serializable
public data class MemberVerificationGuildData(
    val id: Snowflake,
    val name: String,
    val icon: String? = null,
    val description: String? = null,
    val splash: String? = null,
    val discoverySplash: String? = null,
    val homeHeader: String? = null,
    val verificationLevel: VerificationLevel,
    val features: List<GuildFeature>,
    val emojis: List<Snowflake>,
    val approximateMemberCount: Int,
    val approximatePresenceCount: Int,
) {
    public companion object {
        public fun from(entity: DiscordMemberVerificationGuild): MemberVerificationGuildData = with(entity) {
            MemberVerificationGuildData(
                id = id,
                name = name,
                icon = icon,
                description = description,
                splash = splash,
                discoverySplash = discoverySplash,
                homeHeader = homeHeader,
                verificationLevel = verificationLevel,
                features = features,
                emojis = emojis,
                approximateMemberCount = approximateMemberCount,
                approximatePresenceCount = approximatePresenceCount
            )
        }
    }
}

@KordPreview
public fun DiscordMemberVerificationGuild.toData(): MemberVerificationGuildData = MemberVerificationGuildData.from(this)