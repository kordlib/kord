package com.gitlab.kordlib.common

import kotlin.math.min

class BitSet(size: Int = 0) {
    private var data = LongArray(size)
    val size: Int
        get() = data.size * WIDTH

    val isEmpty: Boolean
        get() = data.all { it == 0L }

    val indices: IntRange
        get() = 0 until size

    private var cursor = size - 1


    fun and(another: BitSet) {
        if (another.size > size) copyOf(another.size)
        for (i in another.data.indices) data[i] = data[i] and another.data[i]
        cursor = size - 1
    }

    fun xor(another: BitSet) {
        if (another.size > size) copyOf(another.size)
        for (i in another.data.indices) data[i] = data[i] xor another.data[i]
        cursor = size - 1
    }

    fun or(another: BitSet) {
        if (another.size > size) copyOf(another.size)
        for (i in another.data.indices) data[i] = data[i] or another.data[i]
        cursor = size - 1
    }


    fun andNot(another: BitSet) {
        flip()
        and(another)
    }

    fun flip(index: Int) {
        this[index] = !this[index]
    }

    fun flip(from: Int = 0, to: Int = size) {
        for (i in from until to) flip(i)
    }


    private fun copyOf(newSize: Int = size) {
        val width = (newSize + 63) / WIDTH
        data = data.copyOf(width)
    }

    fun add(another: BitSet) {
        val available = size - cursor + 1
        if (available < another.size) copyOf(another.size - available)
        for (i in indices) {
            this[cursor + i + 1] = another[i]
        }
        cursor = size - 1
    }

    operator fun get(index: Int): Boolean {
        val indexOfWidth = index / WIDTH
        val bitIndex = index % WIDTH
        return data[indexOfWidth] and ((1L shl bitIndex)) != 0L
    }


    operator fun set(index: Int, value: Boolean) {
        val bit = if (value) 1L else 0L
        val indexOfWidth = index / WIDTH
        val bitIndex = index % WIDTH

        if (index !in indices) {
            copyOf(index + 1)
            cursor = index
        }

        data[indexOfWidth] = data[indexOfWidth] or (bit shl bitIndex)
    }


    internal operator fun set(index: Int, value: Long) {
        if (index !in data.indices) {
            val newSize = index + 1
            data.copyOf(newSize)
            cursor = (newSize - 1) * WIDTH
        }
        data[index] = value
    }

    fun set(from: Int = 0, to: Int = size, value: Boolean = true) {
        for (i in from until to) this[i] = value
    }


    operator fun contains(other: BitSet): Boolean {
        if (size < other.size && other.data.sumOf(size, other.size) != 0L) return false
        for (i in 0 until min(size, other.size)) {
            if (!other[i]) continue
            if (!this[i]) return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BitSet) return false
        val widthSize = data.size
        val otherWidthSize = other.data.size
        if (widthSize < otherWidthSize && other.data.sumOf(widthSize, otherWidthSize) != 0L) return false
        for (i in 0 until min(widthSize, otherWidthSize)) {
            if (data[i] != other.data[i]) return false
        }
        return true
    }


    override fun toString(): String {
        val builder = StringBuilder(size)

        for (i in indices) {
            val bit = if (this[i]) 1 else 0
            builder.append(bit)
        }
        return "BitSet(0b${builder.reverse()})"
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }


    //[MSB 1000001110000001111 LSB,1111011101010111, [adding occurs here] 1110101011101 LSB]
    companion object {
        const val WIDTH = Long.SIZE_BITS
    }
}

fun bitSetOf(vararg bits: Long): BitSet {
    val set = BitSet(bits.size)

    for (i in bits.indices) {
        set[i] = bits[i]
    }
    return set
}

internal fun LongArray.sumOf(from: Int, to: Int): Long {
    var sum = 0L
    for (i in from until to) sum += this[i]
    return sum
}
