package com.gitlab.hopebaron.common.annotation

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
