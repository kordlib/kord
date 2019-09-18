package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.core.Kord
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

interface Event : CoroutineScope {
    val kord: Kord

    override val coroutineContext: CoroutineContext
        get() = kord.coroutineContext
}