@file:OptIn(ExperimentalMultiplatform::class)

package dev.kord.test

import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Ignores this Test on JS platforms.
 */
@Target(AnnotationTarget.FUNCTION, CLASS)
@OptionalExpectation
expect annotation class IgnoreOnJs()

/**
 * Ignores this test on JVM.
 */
@Target(AnnotationTarget.FUNCTION, CLASS)
@OptionalExpectation
expect annotation class IgnoreOnJvm()
