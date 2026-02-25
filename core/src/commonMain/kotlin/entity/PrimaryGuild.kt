package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.PrimaryGuildData

/**
 * An instance of a [Primary Guild](https://discord.com/developers/docs/resources/user#user-object-user-primary-guild)
 */
public class PrimaryGuild(
    public val data: PrimaryGuildData,
    override val kord: Kord
) : KordObject {
    /**
     * The ID of the user's primary guild.
     */
    public val identityGuildId: Snowflake? get() = data.identityGuildId

    /**
     * Whether the user is displaying the primary guild's server tag.
     *
     * This can be `null` if the system clears the identity (i.e. server no long supports tags).
     * This will be `false` if teh user manually removes their tag
     */
    public val identityEnabled: Boolean? get() = data.identityEnabled

    /**
     * The text of the user's server tag. Limited to 4 characters
     */
    public val tag: String? get() = data.tag

    /**
     * The server tag badge hash
     */
    public val badgeHash: String? = data.badge

    /**
     * The server tag badge as an Asset
     */
    public val badge: Asset? = badgeHash?.let { identityGuildId?.let { guildId -> Asset.tagBadge(guildId, it, kord) } }
}