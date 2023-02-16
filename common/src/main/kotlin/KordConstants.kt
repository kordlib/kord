// No const vals, they are inlined, so recompiling would be required when values change.
@file:Suppress("MayBeConstant")

package dev.kord.common

import dev.kord.common.annotation.KordExperimental
import kotlin.DeprecationLevel.WARNING

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

    @Deprecated("Renamed to 'VERSION'.", ReplaceWith("KordConstants.VERSION"), level = WARNING)
    @KordExperimental
    public val KORD_VERSION: String get() = VERSION

    @Deprecated("Renamed to 'COMMIT_HASH'.", ReplaceWith("KordConstants.COMMIT_HASH"), level = WARNING)
    @KordExperimental
    public val KORD_COMMIT_HASH: String get() = COMMIT_HASH

    @Deprecated("Renamed to 'SHORT_COMMIT_HASH'.", ReplaceWith("KordConstants.SHORT_COMMIT_HASH"), level = WARNING)
    @KordExperimental
    public val KORD_SHORT_COMMIT_HASH: String get() = SHORT_COMMIT_HASH

    @Deprecated("Renamed to 'GITHUB_URL'.", ReplaceWith("KordConstants.GITHUB_URL"), level = WARNING)
    @KordExperimental
    public val KORD_GITHUB_URL: String get() = GITHUB_URL
}
