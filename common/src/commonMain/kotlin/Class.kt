package dev.kord.common

import dev.kord.common.annotation.KordInternal
import kotlin.reflect.KClass

@Suppress("unused")
public expect class Class<T>

@KordInternal
public expect val <T : Any> KClass<T>.java: Class<T>?
