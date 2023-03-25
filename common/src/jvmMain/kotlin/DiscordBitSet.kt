@file:JvmName("DiscordBitSetJvm")
package dev.kord.common

import java.math.BigInteger

internal actual fun formatIntegerFromByteArray(data: ByteArray): String = BigInteger(data).toString()
internal actual fun parseIntegerToByteArray(value: String): ByteArray = BigInteger(value).toByteArray()
