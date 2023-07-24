package dev.kord.common

import dev.kord.common.annotation.KordInternal
import kotlin.reflect.KClass
import kotlin.jvm.java as getJavaClass

/** @suppress */
public actual typealias Class<T> = java.lang.Class<T>

/** @suppress */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.InlineOnly
@KordInternal
public actual inline val <T : Any> KClass<T>.java: Class<T>? inline get() = getJavaClass
