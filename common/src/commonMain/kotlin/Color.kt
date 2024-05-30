package dev.kord.common

import kotlin.jvm.JvmInline
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@JvmInline
@Serializable(with = Color.Serializer::class)
public value class Color(private val packedRGB: Int) {
    public constructor(red: Int, green: Int, blue: Int) : this(rgb(red, green, blue))

    // removes alpha channel if present
    public val rgb: Int get() = packedRGB and 0xFFFFFF
    public val red: Int get() = (rgb shr 16) and 0xFF
    public val green: Int get() = (rgb shr 8) and 0xFF
    public val blue: Int get() = (rgb shr 0) and 0xFF

    init {
        requireInRange("RGB", rgb, acceptableColorRange)
    }

    override fun toString(): String = "Color(red=$red, green=$green, blue=$blue)"

    public companion object {
        private const val MIN_COLOR = 0
        private const val MAX_COLOR = 0xFFFFFF
        private val acceptableComponentRange = 0..255
        private val acceptableColorRange = MIN_COLOR..MAX_COLOR

        private fun rgb(red: Int, green: Int, blue: Int): Int {
            requireInRange("Red", red, acceptableComponentRange)
            requireInRange("Green", green, acceptableComponentRange)
            requireInRange("Blue", blue, acceptableComponentRange)

            return red and 0xFF shl 16 or
                    (green and 0xFF shl 8) or
                    (blue and 0xFF) shl 0
        }

        private fun requireInRange(name: String, value: Int, range: IntRange) {
            require(value in range) { "$name should be in range of $range but was $value" }
        }
    }

    internal object Serializer : KSerializer<Color> {
        override val descriptor = PrimitiveSerialDescriptor("dev.kord.common.Color", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeInt())
        override fun serialize(encoder: Encoder, value: Color) = encoder.encodeInt(value.rgb)
    }
}
