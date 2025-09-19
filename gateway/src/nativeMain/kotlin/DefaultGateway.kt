package dev.kord.gateway

import dev.kord.common.annotation.KordInternal
import io.ktor.util.network.*
import kotlin.experimental.ExperimentalNativeApi

@KordInternal
public actual fun Throwable.isTimeout(): Boolean = this is UnresolvedAddressException

@OptIn(ExperimentalNativeApi::class)
internal actual val os: String get() = Platform.osFamily.name
