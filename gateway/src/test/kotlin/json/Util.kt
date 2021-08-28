package json

import kotlin.test.assertEquals

infix fun <T> T.shouldBe(that: T) {
    assertEquals(that, this)
}

fun ULong.toNumber() = ULongNumber(this)

class ULongNumber(val value: ULong) : Number(), Comparable<ULongNumber> {
    override fun toByte() = value.toByte()
    override fun toShort() = value.toShort()
    override fun toInt() = value.toInt()
    override fun toLong() = value.toLong()
    override fun toFloat() = value.toFloat()
    override fun toDouble() = value.toDouble()
    override fun toChar() = value.toInt().toChar()
    override fun compareTo(other: ULongNumber) = this.value.compareTo(other.value)
    override fun equals(other: Any?) = other is ULongNumber && this.value == other.value
    override fun hashCode() = value.hashCode()
    override fun toString() = value.toString()
}
