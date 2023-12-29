package dev.kord.common

import dev.kord.common.annotation.KordInternal
import kotlin.reflect.KClass

/** @suppress */
@Suppress("ACTUAL_WITHOUT_EXPECT")
public actual typealias Class<T> = KClass<T>

/** @suppress */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.InlineOnly
@KordInternal
public actual inline val <T : Any> KClass<T>.java: Class<T> inline get() = this
