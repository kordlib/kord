// No const vals, they are inlined, so recompiling would be required when values change.
@file:Suppress("MayBeConstant")

package dev.kord.common

public object KordConstants {

    /** Kord's version. */
    public val VERSION: String = BUILD_CONFIG_GENERATED_LIBRARY_VERSION

    /** The hash of the commit from which this Kord version was built. */
    public val COMMIT_HASH: String = BUILD_CONFIG_GENERATED_COMMIT_HASH

    /** Short variant of [COMMIT_HASH]. */
    public val SHORT_COMMIT_HASH: String = BUILD_CONFIG_GENERATED_SHORT_COMMIT_HASH

    /** URL for Kord's GitHub repository. */
    public val GITHUB_URL: String = "https://github.com/kordlib/kord"

    /** Kord's value for the [User Agent header](https://discord.com/developers/docs/reference#user-agent). */
    public val USER_AGENT: String = "DiscordBot ($GITHUB_URL, $VERSION)"
}
