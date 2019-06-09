package com.gitlab.hopebaron.websocket.retry

interface Retry {
    val hasNext: Boolean
    fun reset()
    suspend fun retry()
}
