package dev.kord.voice

import dev.kord.common.annotation.KordVoice
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordVoice
public enum class SpeakingFlag(public val code: Int) {
    Microphone(1 shl 0),
    Soundshare(1 shl 1),
    Priority(1 shl 2)
}

@KordVoice
@Serializable(with = SpeakingFlags.Serializer::class)
public class SpeakingFlags internal constructor(public val code: Int) {
    public val flags: List<SpeakingFlag> = SpeakingFlag.values().filter { code and it.code != 0 }

    public operator fun contains(flag: SpeakingFlags): Boolean = flag.code and this.code == flag.code

    public operator fun contains(flags: SpeakingFlag): Boolean = flags.code and this.code == flags.code

    public operator fun plus(flags: SpeakingFlags): SpeakingFlags = SpeakingFlags(this.code or flags.code)

    public operator fun plus(flags: SpeakingFlag): SpeakingFlags = SpeakingFlags(this.code or flags.code)

    public operator fun minus(flags: SpeakingFlags): SpeakingFlags = SpeakingFlags(this.code xor flags.code)

    public operator fun minus(flags: SpeakingFlag): SpeakingFlags = SpeakingFlags(this.code xor flags.code)

    public inline fun copy(block: Builder.() -> Unit): SpeakingFlags {
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

    public class Builder(internal var code: Int = 0) {
        public operator fun SpeakingFlag.unaryPlus() {
            this@Builder.code = this@Builder.code or code
        }

        public operator fun SpeakingFlag.unaryMinus() {
            if (this@Builder.code and code == code) {
                this@Builder.code = this@Builder.code xor code
            }
        }

        public operator fun SpeakingFlags.unaryPlus() {
            this@Builder.code = this@Builder.code or code
        }

        public operator fun SpeakingFlags.unaryMinus() {
            if (this@Builder.code and code == code) {
                this@Builder.code = this@Builder.code xor code
            }
        }

        public fun flags(): SpeakingFlags = SpeakingFlags(code)
    }

}

@KordVoice
public inline fun SpeakingFlags(builder: SpeakingFlags.Builder.() -> Unit): SpeakingFlags {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return SpeakingFlags.Builder().apply(builder).flags()
}

@KordVoice
public fun SpeakingFlags(vararg flags: SpeakingFlag): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}

@KordVoice
public fun SpeakingFlags(vararg flags: SpeakingFlags): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}

@KordVoice
public fun SpeakingFlags(flags: Iterable<SpeakingFlag>): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}

@KordVoice
@JvmName("SpeakingFlagsWithIterable")
public fun SpeakingFlags(flags: Iterable<SpeakingFlags>): SpeakingFlags = SpeakingFlags {
    flags.forEach { +it }
}
