@file:OptIn(ExperimentalMultiplatform::class)

package dev.kord.test

import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

/** Ignores this test on JS platforms. */
@Target(CLASS, FUNCTION)
@OptionalExpectation
expect annotation class IgnoreOnJs()

/** Ignores this test on the JVM. */
@Target(CLASS, FUNCTION)
@OptionalExpectation
expect annotation class IgnoreOnJvm()

/** Ignores this test on Native platforms. */
@Target(CLASS, FUNCTION)
@OptionalExpectation
expect annotation class IgnoreOnNative()

/** Ignores this test on simulator platforms. */
@Target(CLASS, FUNCTION)
@OptionalExpectation
expect annotation class IgnoreOnSimulatorPlatforms()
