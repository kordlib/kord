package dev.kord.common.annotation

/**
 * Dsl marker for Kord dsls.
 */
@DslMarker
@Target(AnnotationTarget.CLASS)
public annotation class KordDsl

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
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
public annotation class KordPreview

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
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
public annotation class KordExperimental

/**
 * Marks a Kord-voice related API as experimental.
 * Kord voice is experimental and has **no** backward compatibility guarantees, including both binary and source
 * compatibility. Its API and semantics can and will be changed in next releases.
 *
 * Features marked with this annotation will have its use evaluated and changed over time, and might not make it
 * into the stable api.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
public annotation class KordVoice

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
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS, AnnotationTarget.PROPERTY)
public annotation class KordUnsafe

/**
 * Marks the annotated declaration as deprecated since [version].
 */
@MustBeDocumented
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPEALIAS
)
public annotation class DeprecatedSinceKord(val version: String)
