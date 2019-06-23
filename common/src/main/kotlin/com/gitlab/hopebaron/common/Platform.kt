package com.gitlab.hopebaron.common

import java.time.Instant

object Platform {
    fun nowMillis(): Long {
        return Instant.now().toEpochMilli()
    }
}