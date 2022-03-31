// No const vals, they are inlined, so recompiling would be required when values change.
@file:Suppress("MayBeConstant")

package dev.kord.common

import dev.kord.common.annotation.KordExperimental

@KordExperimental
public object KordConstants {

    /** Kord's version. */
    @KordExperimental
    public val KORD_VERSION: String = BUILD_CONFIG_GENERATED_LIBRARY_VERSION

    /** The hash of the commit from which this Kord version was built. */
    @KordExperimental
    public val KORD_COMMIT_HASH: String = BUILD_CONFIG_GENERATED_COMMIT_HASH

    /** Short variant of [KORD_COMMIT_HASH]. */
    @KordExperimental
    public val KORD_SHORT_COMMIT_HASH: String = BUILD_CONFIG_GENERATED_SHORT_COMMIT_HASH

    /** URL for Kord's GitHub repository. */
    @KordExperimental
    public val KORD_GITHUB_URL: String = "https://github.com/kordlib/kord"

    /** Kord's value for the [User Agent header](https://discord.com/developers/docs/reference#user-agent). */
    @KordExperimental
    public val USER_AGENT: String = "DiscordBot ($KORD_GITHUB_URL, $KORD_VERSION)"
}
