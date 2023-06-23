package dev.kord.common

import java.math.BigInteger
import java.nio.ByteBuffer

internal actual fun formatIntegerFromLittleEndianLongArray(data: LongArray): String {
    // need to convert from little-endian data to big-endian expected by BigInteger
    val buffer = ByteBuffer.allocate(data.size * Long.SIZE_BYTES)
    buffer.asLongBuffer().put(data.reversedArray())
    return BigInteger(buffer.array()).toString()
}

internal actual fun parseIntegerToBigEndianByteArray(value: String): ByteArray = BigInteger(value).toByteArray()
