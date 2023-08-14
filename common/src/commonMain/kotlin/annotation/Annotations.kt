package dev.kord.common.annotation

import kotlin.RequiresOptIn.Level.ERROR
import kotlin.RequiresOptIn.Level.WARNING
import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.*

/** [DslMarker] for Kord DSLs. */
@DslMarker
@Retention(BINARY)
@Target(CLASS)
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
@RequiresOptIn(level = WARNING)
@Retention(BINARY)
@Target(CLASS, PROPERTY, CONSTRUCTOR, FUNCTION, TYPEALIAS)
public annotation class KordPreview

/**
 * Marks a Kord-related API as experimental.
 *
 * Kord experimental has **no** backward compatibility guarantees, including both binary and source compatibility.
 * Its API and semantics can and will be changed in next releases.
 *
 * Features marked with this annotation will have its use evaluated and changed over time, and might not make it
 * into the stable api.
 */
@MustBeDocumented
@RequiresOptIn(level = WARNING)
@Retention(BINARY)
@Target(CLASS, PROPERTY, FUNCTION, TYPEALIAS)
public annotation class KordExperimental

/**
 * Marks a Kord-voice related API as experimental.
 *
 * Kord voice is experimental and has **no** backward compatibility guarantees, including both binary and source
 * compatibility. Its API and semantics can and will be changed in next releases.
 *
 * Features marked with this annotation will have its use evaluated and changed over time, and might not make it
 * into the stable api.
 */
@MustBeDocumented
@RequiresOptIn(level = WARNING)
@Retention(BINARY)
@Target(CLASS, PROPERTY, FUNCTION, TYPEALIAS)
public annotation class KordVoice

/**
 * Marks a Kord-related API as potentially unsafe.
 *
 * Kord marks targets as unsafe if it exposes functionality in a way that is more error-prone than alternatives
 * and can lead to *inconsistent state* and *fail silent* or *fail slow* code.
 *
 * The trade-off is usually increased performance by reducing cache hits and requests to the discord api.
 *
 * Functionality that is annotated with [KordUnsafe] should suggest a safer alternative approach in its documentation.
 */
@MustBeDocumented
@RequiresOptIn("This API is potentially unsafe.", level = WARNING)
@Retention(BINARY)
@Target(CLASS, PROPERTY, FUNCTION, PROPERTY_SETTER, TYPEALIAS)
public annotation class KordUnsafe

/**
 * Marks the annotated declaration as deprecated since [version].
 *
 * These declarations must also be annotated with [Deprecated].
 */
@MustBeDocumented
@Retention(BINARY)
@Target(CLASS, ANNOTATION_CLASS, PROPERTY, CONSTRUCTOR, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, TYPEALIAS)
public annotation class DeprecatedSinceKord(val version: String)

/**
 * Marks an API for internal use only.
 */
@MustBeDocumented
@RequiresOptIn("This API is intended for internal use only.", level = ERROR)
@Retention(BINARY)
@Target(CLASS, PROPERTY, FUNCTION, TYPEALIAS)
public annotation class KordInternal
