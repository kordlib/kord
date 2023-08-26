package dev.kord.common

import dev.kord.common.annotation.KordInternal
import kotlin.reflect.KClass

// TODO remove when enum artifacts are removed from generated flags

/** @suppress */
@Suppress("unused")
public expect class Class<T>

/** @suppress */
@KordInternal
public expect val <T : Any> KClass<T>.java: Class<T>
