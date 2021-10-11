package dev.kord.voice.io

/**
 * A lightweight read-only view of a ByteArray.
 */
class ByteArrayView private constructor(val data: ByteArray, start: Int, end: Int) : Iterable<Byte> {
    companion object {
        fun from(data: ByteArray, start: Int, end: Int) =
            if ((0 <= start && start <= data.size) && (start <= end && end <= data.size)) ByteArrayView(
                data,
                start,
                end
            )
            else null
    }

    var dataStart: Int = start
        private set

    var dataEnd: Int = end
        private set

    val viewSize: Int get() = dataEnd - dataStart

    operator fun get(index: Int): Byte {
        if (dataStart + index > dataEnd) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        return data[dataStart + index]
    }

    private class ByteArrayViewIterator(private val view: ByteArrayView) : Iterator<Byte> {
        private var index = 0

        override fun hasNext() = index < view.viewSize

        fun nextByte(): Byte = try {
            view[index++]
        } catch (e: ArrayIndexOutOfBoundsException) {
            index -= 1
            throw NoSuchElementException(e.message)
        }

        override fun next() = nextByte()
    }

    override operator fun iterator(): Iterator<Byte> = ByteArrayViewIterator(this)

    fun asShort(): Short {
        require(viewSize == 2) { "this view must be equal to 2 bytes to read as short. instead the size is $viewSize" }

        var value = 0
        repeat(2) {
            value = value.concat(this[it])
        }

        return value.toShort()
    }

    fun asInt(): Int {
        require(viewSize == 4) { "this view must be equal to 4 bytes to read as int. instead the size is $viewSize" }

        var value = 0
        repeat(4) {
            value = value.concat(this[it])
        }

        return value
    }

    fun resize(start: Int = this.dataStart, end: Int = this.dataEnd): Boolean {
        // check if the start and end is within bounds and are in the correct order
        return if (start >= 0 && end <= data.size) {
            this.dataStart = start
            this.dataEnd = end

            true
        } else {
            false
        }
    }

    fun view(start: Int = dataStart, end: Int = dataEnd): ByteArrayView? {
        return from(data, start, end)
    }

    /**
     * Create a new [ByteArray] that's data contains only this view.
     */
    fun toByteArray(): ByteArray {
        return data.copyOfRange(dataStart, dataEnd)
    }

    /**
     * Creates a new [ByteArrayView] that's data contains only this view.
     */
    fun clone(): ByteArrayView {
        return toByteArray().view()
    }

    private fun Int.concat(other: Byte): Int {
        return (this shl 8) + other
    }
}

fun ByteArray.view(start: Int, end: Int) = ByteArrayView.from(this, start, end)
fun ByteArray.view() = view(0, size)!!