package dev.kord.ksp

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Marks this class as available for reflection on GraalVM Native Image.
 *
 * **This is only needed for types not annotated with `@kotlinx.serialization.Serializable`.**
 */
@Retention(SOURCE)
@Target(CLASS)
annotation class AvailableForReflectionOnGraalVMNativeImage
