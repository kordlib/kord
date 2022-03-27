// No const vals, they are inlined, so recompiling would be required when values change.
@file:Suppress("MayBeConstant")

package dev.kord.common

public object KordConstants {

    /** Kord's version. */
    public val KORD_VERSION: String = BUILD_CONFIG_GENERATED_LIBRARY_VERSION

    /** URL for Kord's GitHub repository. */
    public val KORD_GITHUB_URL: String = "https://github.com/kordlib/kord"

    /** Kord's value for the [User Agent header](https://discord.com/developers/docs/reference#user-agent). */
    public val USER_AGENT: String = "DiscordBot ($KORD_GITHUB_URL, $KORD_VERSION)"
}
