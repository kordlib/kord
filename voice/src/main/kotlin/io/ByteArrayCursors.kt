package dev.kord.voice.io

import io.ktor.utils.io.bits.*
import kotlinx.atomicfu.atomic

/**
 * A light-weight mutable cursor for a ByteArray.
 */
public class MutableByteArrayCursor(data: ByteArray) {
    public var data: ByteArray = data
        private set

    public var cursor: Int by atomic(0)

    public val remaining: Int
        get() = data.size - cursor

    public val isExhausted: Boolean = cursor == data.size + 1

    public fun reset() {
        cursor = 0
    }

    public fun resize(newSize: Int, ifSmaller: Boolean = false): Boolean {
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

    public fun writeByte(b: Byte) {
        isNotExhaustedOrThrow()

        data[cursor] = b
        cursor++
    }

    public fun writeByteArray(array: ByteArray, offset: Int = 0, length: Int = array.size) {
        if (length > remaining) error("$remaining bytes remaining. tried to write $length bytes")

        array.copyInto(data, cursor, offset, offset + length)

        cursor += length
    }

    public fun writeByteView(view: ByteArrayView): Unit = writeByteArray(view.data, view.dataStart, view.viewSize)

    public fun writeShort(s: Short) {
        var value = s.reverseByteOrder().toInt()

        repeat(2) {
            writeByte(value.toByte()) // write the least-significant byte
            value = value shr 8       // shift to new byte
        }
    }

    public fun writeInt(i: Int) {
        var value = i.reverseByteOrder()

        repeat(4) {
            writeByte(value.toByte())  // write the least-significant byte
            value = value shr 8        // shift to new byte
        }
    }
}

public fun ByteArray.mutableCursor(): MutableByteArrayCursor = MutableByteArrayCursor(this)
public fun ByteArrayView.mutableCursor(): MutableByteArrayCursor =
    MutableByteArrayCursor(data).also { it.cursor = dataStart }

public fun MutableByteArrayCursor.writeByteArrayOrResize(data: ByteArray) {
    if (remaining < data.size)
        resize(data.size + this.data.size)

    writeByteArray(data)
}

public fun MutableByteArrayCursor.writeByteViewOrResize(view: ByteArrayView) {
    if (remaining < view.viewSize)
        resize(view.viewSize + data.size)

    writeByteView(view)
}

/**
 * A lightweight read-only cursor for a ByteArrayView.
 */
public class ReadableByteArrayCursor(public val view: ByteArrayView) {
    public var cursor: Int by atomic(0)

    public val remaining: Int get() = view.data.size - cursor

    private fun hasEnoughOrThrow(n: Int) {
        if (view.viewSize >= cursor + n) return
        else error("not enough bytes")
    }

    public fun readByte(): Byte {
        hasEnoughOrThrow(1)

        return view[cursor++]
    }

    public fun consume(n: Int) {
        cursor += n
    }

    public fun readBytes(n: Int): ByteArrayView {
        hasEnoughOrThrow(n)

        val view = view.view(view.dataStart + cursor, view.dataStart + cursor + n)!!
        cursor += n

        return view
    }

    public fun readShort(): Short {
        hasEnoughOrThrow(2)

        return readBytes(2).asShort()
    }

    public fun readInt(): Int {
        hasEnoughOrThrow(4)

        return readBytes(4).asInt()
    }

    public fun readRemaining(): ByteArrayView {
        return readBytes(remaining)
    }
}

public fun ByteArray.readableCursor(): ReadableByteArrayCursor = view().readableCursor()
public fun ByteArrayView.readableCursor(): ReadableByteArrayCursor = ReadableByteArrayCursor(this)