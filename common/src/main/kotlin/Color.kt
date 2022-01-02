package dev.kord.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = Color.Serializer::class)
public class Color(rgb: Int) {
    public constructor(red: Int, green: Int, blue: Int) : this(rgb(red, green, blue))

    public val rgb: Int = rgb and 0xFFFFFF

    public val red: Int get() = (rgb shr 16) and 0xFF
    public val green: Int get() = (rgb shr 8) and 0xFF
    public val blue: Int get() = (rgb shr 0) and 0xFF

    init {
        require(this.rgb in MIN_COLOR..MAX_COLOR) { "RGB should be in range of $MIN_COLOR..$MAX_COLOR but was ${this.rgb}" }
    }

    override fun toString(): String = "Color(red=$red,green=$green,blue=$blue)"

    override fun hashCode(): Int = rgb.hashCode()

    override fun equals(other: Any?): Boolean {
        val color = other as? Color ?: return false

        return color.rgb == rgb
    }

    public companion object {
        private const val MIN_COLOR = 0
        private const val MAX_COLOR = 0xFFFFFF
    }

    internal object Serializer : KSerializer<Color> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.color", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: Color) {
            encoder.encodeInt(value.rgb)
        }
    }
}

private fun rgb(red: Int, green: Int, blue: Int): Int {
    require(red in 0..255) { "Red should be in range of 0..255 but was $red" }
    require(green in 0..255) { "Green should be in range of 0..255 but was $green" }
    require(blue in 0..255) { "Blue should be in range of 0..255 but was $blue" }


    return red and 0xFF shl 16 or
            (green and 0xFF shl 8) or
            (blue and 0xFF) shl 0
}

public val java.awt.Color.kColor: Color get() = Color(rgb)
