@file:JvmName("DiscordBitSetJvm")
package dev.kord.common

import java.math.BigInteger

internal actual fun formatBigEndianIntegerFromByteArray(data: ByteArray): String = BigInteger(data).toString()
internal actual fun parseBigEndianIntegerToByteArray(value: String): ByteArray = BigInteger(value).toByteArray()
