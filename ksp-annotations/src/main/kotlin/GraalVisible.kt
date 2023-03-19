package dev.kord.ksp

/**
 * Marks this class as visible via Reflection in Graal.
 *
 * **This is only needed for types not annotated with [Serializable]**
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class GraalVisible
