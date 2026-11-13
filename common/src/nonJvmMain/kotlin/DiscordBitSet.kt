package dev.kord.common

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

internal actual fun formatIntegerFromLittleEndianLongArray(data: LongArray): String {
    // need to convert from little-endian data to big-endian expected by BigInteger
    val bytes = ByteArray(size = data.size * Long.SIZE_BYTES)
    val lastIndex = data.lastIndex
    for (i in 0..lastIndex) {
        val offset = (lastIndex - i) * Long.SIZE_BYTES
        val long = data[i]
        bytes[offset] = (long ushr 56).toByte()
        bytes[offset + 1] = (long ushr 48).toByte()
        bytes[offset + 2] = (long ushr 40).toByte()
        bytes[offset + 3] = (long ushr 32).toByte()
        bytes[offset + 4] = (long ushr 24).toByte()
        bytes[offset + 5] = (long ushr 16).toByte()
        bytes[offset + 6] = (long ushr 8).toByte()
        bytes[offset + 7] = long.toByte()
    }
    return BigInteger.fromByteArray(bytes, Sign.POSITIVE).toString()
}

internal actual fun parseNonNegativeIntegerToBigEndianByteArray(value: String): ByteArray = BigInteger
    .parseString(value)
    .also { if (it.isNegative) throw NumberFormatException("Invalid DiscordBitSet format: '$value'") }
    .toByteArray()
