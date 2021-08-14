package dev.kord.voice

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

enum class SpeakingFlag(val code: Int) {
    Microphone(1 shl 0),
    Soundshare(1 shl 1),
    Priority(1 shl 2)
}

@Serializable(with = SpeakingFlags.Serializer::class)
class SpeakingFlags internal constructor(val code: Int) {
    val flags = SpeakingFlag.values().filter { code and it.code != 0 }

    operator fun contains(flag: SpeakingFlags) = flag.code and this.code == flag.code

    operator fun contains(flags: SpeakingFlag) = flags.code and this.code == flags.code

    operator fun plus(flags: SpeakingFlags): SpeakingFlags = SpeakingFlags(this.code or flags.code)

    operator fun plus(flags: SpeakingFlag): SpeakingFlags = SpeakingFlags(this.code or flags.code)

    operator fun minus(flags: SpeakingFlags): SpeakingFlags = SpeakingFlags(this.code xor flags.code)

    operator fun minus(flags: SpeakingFlag): SpeakingFlags = SpeakingFlags(this.code xor flags.code)


    inline fun copy(block: Builder.() -> Unit): SpeakingFlags {
        val builder = Builder(code)
        builder.apply(block)
        return builder.flags()
    }

    override fun toString(): String = "SpeakingFlags(flags=$flags)"

    internal object Serializer : KSerializer<SpeakingFlags> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("flags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): SpeakingFlags {
            val flags = decoder.decodeInt()
            return SpeakingFlags(flags)
        }

        override fun serialize(encoder: Encoder, value: SpeakingFlags) {
            encoder.encodeInt(value.code)
        }
    }

    class Builder(internal var code: Int = 0) {
        operator fun SpeakingFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or code
        }

        operator fun SpeakingFlag.unaryMinus() {
            if (this@Builder.code and code == code) {
                this@Builder.code = this@Builder.code xor code
            }
        }

        operator fun SpeakingFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or code
        }

        operator fun SpeakingFlags.unaryMinus() {
            if (this@Builder.code and code == code) {
                this@Builder.code = this@Builder.code xor code
            }
        }

        fun flags() = SpeakingFlags(code)
    }

}

@OptIn(ExperimentalContracts::class)
inline fun SpeakingFlags(builder: SpeakingFlags.Builder.() -> Unit): SpeakingFlags {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return SpeakingFlags.Builder().apply(builder).flags()
}

fun SpeakingFlags(vararg flags: SpeakingFlag) = SpeakingFlags {
    flags.forEach { +it }
}

fun SpeakingFlags(vararg flags: SpeakingFlags) = SpeakingFlags {
    flags.forEach { +it }
}

fun SpeakingFlags(flags: Iterable<SpeakingFlag>) = SpeakingFlags {
    flags.forEach { +it }
}

@JvmName("SpeakingFlagsWithIterable")
fun SpeakingFlags(flags: Iterable<SpeakingFlags>) = SpeakingFlags {
    flags.forEach { +it }
}
