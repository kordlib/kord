package dev.kord.voice.io

import io.ktor.utils.io.bits.*
import kotlinx.atomicfu.atomic

/**
 * A light-weight mutable cursor for a ByteArray.
 */
class MutableByteArrayCursor(data: ByteArray) {
    var data: ByteArray = data
        private set

    var cursor by atomic(0)

    val remaining get() = data.size - cursor

    val isExhausted: Boolean = cursor == data.size + 1

    fun reset() {
        cursor = 0
    }

    fun resize(newSize: Int, ifSmaller: Boolean = false): Boolean {
        return if (data.size < newSize || ifSmaller) {
            val newData = ByteArray(newSize)

            if (newSize < data.size) {
                data.copyInto(newData, 0, 0, newSize)
            } else {
                data.copyInto(newData, 0, 0, data.size)
            }

            true
        } else {
            false
        }
    }

    private fun isNotExhaustedOrThrow() {
        if (isExhausted)
            error("cant write anymore")
        else
            return
    }

    fun writeByte(b: Byte) {
        isNotExhaustedOrThrow()

        data[cursor] = b
        cursor++
    }

    fun writeByteArray(array: ByteArray, offset: Int = 0, length: Int = array.size) {
        if (length > remaining) error("$remaining bytes remaining. tried to write $length bytes")

        array.copyInto(data, cursor, offset, offset + length)

        cursor += length
    }

    fun writeByteView(view: ByteArrayView) = writeByteArray(view.data, view.dataStart, view.viewSize)

    fun writeShort(s: Short) {
        var value = s.reverseByteOrder().toInt()

        repeat(2) {
            writeByte(value.toByte()) // write the least-significant byte
            value = value shr 8       // shift to new byte
        }
    }

    fun writeInt(i: Int) {
        var value = i.reverseByteOrder()

        repeat(4) {
            writeByte(value.toByte())  // write the least-significant byte
            value = value shr 8        // shift to new byte
        }
    }
}

fun ByteArray.mutableCursor() = MutableByteArrayCursor(this)
fun ByteArrayView.mutableCursor() = MutableByteArrayCursor(data).also { it.cursor = dataStart }

fun MutableByteArrayCursor.writeByteArrayOrResize(data: ByteArray) {
    if (remaining < data.size)
        resize(data.size + this.data.size)

    writeByteArray(data)
}

fun MutableByteArrayCursor.writeByteViewOrResize(view: ByteArrayView) {
    if (remaining < view.viewSize)
        resize(view.viewSize + data.size)

    writeByteView(view)
}

/**
 * A lightweight read-only cursor for a ByteArrayView.
 */
class ReadableByteArrayCursor(val view: ByteArrayView) {
    var cursor: Int by atomic(0)

    val remaining: Int get() = view.data.size - cursor

    private fun hasEnoughOrThrow(n: Int) {
        if (view.viewSize >= cursor + n) return
        else error("not enough bytes")
    }

    fun readByte(): Byte {
        hasEnoughOrThrow(1)

        return view[cursor++]
    }

    fun consume(n: Int) {
        cursor += n
    }

    fun readBytes(n: Int): ByteArrayView {
        hasEnoughOrThrow(n)

        val view = view.view(view.dataStart + cursor, view.dataStart + cursor + n)!!
        cursor += n

        return view
    }

    fun readShort(): Short {
        hasEnoughOrThrow(2)

        return readBytes(2).asShort()
    }

    fun readInt(): Int {
        hasEnoughOrThrow(4)

        return readBytes(4).asInt()
    }

    fun readRemaining(): ByteArrayView {
        return readBytes(remaining)
    }
}

fun ByteArray.readableCursor() = view().readableCursor()
fun ByteArrayView.readableCursor() = ReadableByteArrayCursor(this)