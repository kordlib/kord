package dev.kord.voice.util

import dev.kord.voice.io.ByteArrayView

@OptIn(ExperimentalUnsignedTypes::class)
internal fun ByteArrayView.toUByteArray() = toByteArray().asUByteArray()
