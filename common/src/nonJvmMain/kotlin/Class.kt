package dev.kord.common

import dev.kord.common.annotation.KordInternal
import kotlin.reflect.KClass

// just use some nonsensical type, we always return null
public actual typealias Class<T> = ArrayList<T>

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.InlineOnly
@KordInternal
public actual inline val <T : Any> KClass<T>.java: Class<T>? inline get() = null
