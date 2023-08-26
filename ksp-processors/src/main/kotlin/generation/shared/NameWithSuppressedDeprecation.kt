package dev.kord.ksp.generation.shared

import dev.kord.ksp.generation.GenerationEntity
import kotlin.DeprecationLevel.*

internal val GenerationEntity.Entry.nameWithSuppressedDeprecation
    get() = when (deprecated?.level) {
        null -> name
        WARNING -> """@Suppress("DEPRECATION")·$name"""
        ERROR, HIDDEN -> """@Suppress("DEPRECATION_ERROR")·$name"""
    }
