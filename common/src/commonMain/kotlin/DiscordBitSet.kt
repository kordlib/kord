package dev.kord.common

import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.internal.*
import io.ktor.utils.io.pool.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.max
import kotlin.math.min

private const val SAFE_LENGTH = 19
private const val WIDTH = Long.SIZE_BITS

@Suppress("FunctionName")
public fun EmptyBitSet(): DiscordBitSet = DiscordBitSet()

internal expect fun formatBigEndianIntegerFromByteArray(data: ByteArray): String
internal expect fun parseBigEndianIntegerToByteArray(value: String): ByteArray

@Serializable(with = DiscordBitSetSerializer::class)
public class DiscordBitSet(internal var data: LongArray) { // data is in little-endian order

    public val isEmpty: Boolean
        get() = data.all { it == 0L }

    public val value: String
        get() {
            // need to convert from little-endian data to big-endian expected by BigInteger
            return withBuffer(data.size * Long.SIZE_BYTES) {
                writeFully(data.reversedArray())
                formatBigEndianIntegerFromByteArray(readBytes())
            }
        }

    public val size: Int
        get() = data.size * WIDTH

    public val binary: String
        get() = data.map { it.toULong().toString(radix = 2).padStart(length = ULong.SIZE_BITS, '0') }
            .reversed()
            .joinToString(separator = "")
            .trimStart('0')
            .ifEmpty { "0" }

    override fun equals(other: Any?): Boolean {
        if (other !is DiscordBitSet) return false
        // trailing zeros are ignored -> getOrZero
        for (i in 0 until max(this.data.size, other.data.size)) {
            if (this.getOrZero(i) != other.getOrZero(i)) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 1
        // trailing zeros are ignored to have the same hashCode for equal bit sets
        for (i in 0..(data.indexOfLast { it != 0L })) {
            result = (31 * result) + data[i].hashCode()
        }
        return result
    }

    private fun getOrZero(i: Int) = data.getOrNull(i) ?: 0L

    public operator fun get(index: Int): Boolean {
        require(index >= 0)
        if (index >= size) return false
        val indexOfWidth = index / WIDTH
        val bitIndex = index % WIDTH
        return data[indexOfWidth] and (1L shl bitIndex) != 0L
    }

    public operator fun contains(other: DiscordBitSet): Boolean {
        for ((index, value) in other.data.withIndex()) {
            if ((this.getOrZero(index) and value) != value) return false
        }
        return true
    }

    public operator fun set(index: Int, value: Boolean) {
        require(index >= 0)
        val indexOfWidth = index / WIDTH
        if (index >= size) data = data.copyOf(indexOfWidth + 1)
        val bitIndex = index % WIDTH
        val prev = data[indexOfWidth]
        data[indexOfWidth] = if (value) prev or (1L shl bitIndex) else prev and (1L shl bitIndex).inv()
    }

    public operator fun plus(another: DiscordBitSet): DiscordBitSet {
        val copy = DiscordBitSet(data.copyOf())
        copy.add(another)
        return copy
    }

    public operator fun minus(another: DiscordBitSet): DiscordBitSet {
        val copy = DiscordBitSet(data.copyOf())
        copy.remove(another)
        return copy
    }

    public fun add(another: DiscordBitSet) {
        if (another.data.size > data.size) data = data.copyOf(another.data.size)
        for (i in another.data.indices) {
            data[i] = data[i] or another.data[i]
        }
    }


    public fun remove(another: DiscordBitSet) {
        for (i in 0 until min(data.size, another.data.size)) {
            data[i] = data[i] xor (data[i] and another.data[i])
        }
    }


    override fun toString(): String {
        return "DiscordBitSet($binary)"
    }

}

public fun DiscordBitSet(vararg widths: Long): DiscordBitSet {
    return DiscordBitSet(widths)
}

public fun DiscordBitSet(value: String): DiscordBitSet {
    if (value.length <= SAFE_LENGTH) {// fast path
        return DiscordBitSet(longArrayOf(value.toULong().toLong()))
    }

    val bytes = parseBigEndianIntegerToByteArray(value)

    val longSize = (bytes.size / Long.SIZE_BYTES) + 1
    val destination = LongArray(longSize)

    var longIndex = -1
    bytes.reversed().forEachIndexed { index, byte ->
        val offset = index % Long.SIZE_BYTES
        if (offset == 0) {
            longIndex += 1
        }

        destination[longIndex] =
            (destination[longIndex].toULong() or (byte.toUByte().toULong() shl offset * Byte.SIZE_BITS)).toLong()
    }

    return DiscordBitSet(destination)
}


public object DiscordBitSetSerializer : KSerializer<DiscordBitSet> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("DiscordBitSet", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DiscordBitSet {
        return DiscordBitSet(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: DiscordBitSet) {
        encoder.encodeString(value.value)
    }
}
