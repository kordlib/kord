@file:JvmName("DiscordBitSetJvm")
package dev.kord.common

import java.math.BigInteger

internal actual fun formatIntegerFromBigEndianByteArray(data: ByteArray): String = BigInteger(data).toString()
internal actual fun parseIntegerToBigEndianByteArray(value: String): ByteArray = BigInteger(value).toByteArray()
