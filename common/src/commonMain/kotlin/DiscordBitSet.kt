package dev.kord.common

import dev.kord.common.serialization.LongOrStringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.max
import kotlin.math.min

private const val SAFE_LENGTH = 19
private const val WIDTH = Long.SIZE_BITS

@Suppress("FunctionName")
public fun EmptyBitSet(): DiscordBitSet = DiscordBitSet()

internal expect fun formatIntegerFromLittleEndianLongArray(data: LongArray): String
internal expect fun parseNonNegativeIntegerToBigEndianByteArray(value: String): ByteArray

@Serializable(with = DiscordBitSet.Serializer::class)
public class DiscordBitSet(internal var data: LongArray) { // data is in little-endian order

    public val isEmpty: Boolean
        get() = data.all { it == 0L }

    public val value: String get() = formatIntegerFromLittleEndianLongArray(data)

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
        for (i in 0..<max(this.data.size, other.data.size)) {
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
        for (i in 0..<min(data.size, another.data.size)) {
            data[i] = data[i] xor (data[i] and another.data[i])
        }
    }


    override fun toString(): String {
        return "DiscordBitSet($binary)"
    }

    public fun copy(): DiscordBitSet = DiscordBitSet(data = data.copyOf())

    internal object Serializer : KSerializer<DiscordBitSet> {
        override val descriptor = PrimitiveSerialDescriptor("dev.kord.common.DiscordBitSet", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: DiscordBitSet) = encoder.encodeString(value.value)
        override fun deserialize(decoder: Decoder) =
            DiscordBitSet(decoder.decodeSerializableValue(LongOrStringSerializer))
    }
}

public fun DiscordBitSet(vararg widths: Long): DiscordBitSet {
    return DiscordBitSet(widths)
}

public fun DiscordBitSet(value: String): DiscordBitSet {
    if (value.length <= SAFE_LENGTH) {// fast path
        return DiscordBitSet(longArrayOf(value.toULong().toLong()))
    }

    val bytes = parseNonNegativeIntegerToBigEndianByteArray(value)

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


@Deprecated(
    "Replaced by 'DiscordBitSet.serializer()'.",
    ReplaceWith("DiscordBitSet.serializer()", imports = ["dev.kord.common.DiscordBitSet"]),
    DeprecationLevel.HIDDEN,
)
public object DiscordBitSetSerializer : KSerializer<DiscordBitSet> by DiscordBitSet.Serializer
