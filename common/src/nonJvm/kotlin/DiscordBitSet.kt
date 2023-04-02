package dev.kord.common

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import io.ktor.utils.io.core.*

internal actual fun formatIntegerFromLittleEndianLongArray(data: LongArray) =
    withBuffer(data.size * Long.SIZE_BYTES) {
        // need to convert from little-endian data to big-endian expected by BigInteger
        writeFully(data.reversedArray())
        BigInteger.fromByteArray(readBytes(), Sign.POSITIVE).toString()
    }

internal actual fun parseIntegerToBigEndianByteArray(value: String): ByteArray =
    BigInteger.parseString(value).toByteArray()
