package com.gitlab.kordlib.common.annotation

/**
 * Dsl marker for Kord dsls.
 */
@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class KordDsl

/**
 * Marks a Kord-related API as a feature preview.
 *
 * A Kord preview has **no** backward compatibility guarantees, including both binary and source compatibility.
 * Its API and semantics can and will be changed in next releases.
 *
 * Features marked with this annotation will have its api evaluated and changed over time.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@Experimental(level = Experimental.Level.WARNING)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
annotation class KordPreview

/**
 * Marks a Kord-related API as experimental.
 *
 * Kord experimental has **no** backward compatibility guarantees, including both binary and source compatibility.
 * Its API and semantics can and will be changed in next releases.
 *
 * Features marked with this annotation will have its use evaluated and changed over time
 * and might not make it into the stable api.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@Experimental(level = Experimental.Level.WARNING)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
annotation class KordExperimental

/**
 * Marks a Kord-related API as potentially unsafe.
 *
 * Kord marks targets as unsafe if it exposes functionality in a way that is more error prone than alternatives
 * and can lead to *inconsistent state* and *fail silent* or *fail slow* code.
 *
 * The trade off is usually increased performance by reducing cache hits and requests to the discord api.
 *
 * Functionality that is annotated with KordUnsafe should suggest a safer alternative approach in its documentation.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@Experimental(level = Experimental.Level.WARNING)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
annotation class KordUnsafe

/**
 * Marks a Kord-related API as unstable.
 *
 * Kord marks targets as unstable if they are subject to changes outside the development process.
 * They offer no binary compatibility between versions.
 *
 * Consumers of this API are advised to lock the Kord dependency to a fixed version as even [patch](https://semver.org/)
 * versions are allowed to make changes to fields annotated with [KordUnstableApi].
 *
 * Compared to [KordExperimental] and [KordPreview], targets annotated with [KordUnstableApi] have no intention
 * to mature into stable and only serve as a warning to the user.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class KordUnstableApi