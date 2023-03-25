package dev.kord.common

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

internal actual fun formatIntegerFromByteArray(data: ByteArray): String =
    BigInteger.fromByteArray(data, Sign.POSITIVE).toString()

internal actual fun parseIntegerToByteArray(value: String): ByteArray = BigInteger.parseString(value).toByteArray()
