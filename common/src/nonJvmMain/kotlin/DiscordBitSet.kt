package dev.kord.common

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

internal actual fun formatIntegerFromLittleEndianLongArray(data: LongArray): String {
    val buffer = Buffer()
    data.reversedArray().forEach(buffer::writeLong)
    return BigInteger.fromByteArray(buffer.readByteArray(), Sign.POSITIVE).toString()
}

internal actual fun parseNonNegativeIntegerToBigEndianByteArray(value: String): ByteArray = BigInteger
    .parseString(value)
    .also { if (it.isNegative) throw NumberFormatException("Invalid DiscordBitSet format: '$value'") }
    .toByteArray()
