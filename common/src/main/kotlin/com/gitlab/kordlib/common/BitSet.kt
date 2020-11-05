package com.gitlab.kordlib.common

import kotlin.math.min

class BitSet(size: Int = 0) {
    private var data = LongArray(size)
    val size: Int
        get() = data.size * WIDTH
    val isEmpty: Boolean
        get() = data.all { it == 0L }

    private var cursor = size - 1


    fun and(another: BitSet) {
        if (another.size > size) copyOf(another.size)
        for (i in another.data.indices) data[i] = data[i] and another.data[i]
    }

    fun xor(another: BitSet) {
        if (another.size > size) copyOf(another.size)
        for (i in another.data.indices) data[i] = data[i] xor another.data[i]
    }

    fun or(another: BitSet) {
        if (another.size > size) copyOf(another.size)
        for (i in another.data.indices) data[i] = data[i] or another.data[i]
    }


    fun andNot(another: BitSet) {
        and(another)
        flip()
    }

    fun flip(index: Int) {
        this[index] = !this[index]
    }

    fun flip(from: Int = 0, to: Int = size) {
        for (i in from until to) flip(i)
    }


    private fun copyOf(newSize: Int = size) {
        val width = if (newSize % WIDTH == 0) newSize / WIDTH else newSize / WIDTH + 1
        data = data.copyOf(width)
    }

    fun add(another: BitSet) {
        val available = size - cursor + 1
        if (available < another.size) copyOf(size + another.size - available)
        for (i in 0 until another.size) {
            this[cursor + i + 1] = another[i]
        }
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
        data[indexOfWidth] = data[indexOfWidth] and ((bit shl bitIndex))
    }

    fun set(from: Int = 0, to: Int = size, value: Boolean = true) {
        for (i in from until to) this[i] = value
    }

    operator fun set(index: Int, value: Long) {
        data[index] = value
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BitSet) return false
        val (longer, shorter) = if (size > other.size) Pair(data, other.data) else Pair(other.data, data)
        val paddedShort = shorter.copyOf(longer.size) // in-case longer had the delta filled with zeros only
        for (i in shorter.indices) {
            if(longer[i] and paddedShort[i] != longer[i]) return false
        }
        return true
    }


    override fun toString(): String {
        val builder = StringBuilder(size)

        for (i in 0 until size) {
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
