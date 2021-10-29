package dev.kord.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = Color.Serializer::class)
class Color(rgb: Int) {
    constructor(red: Int, green: Int, blue: Int) : this(rgb(red, green, blue))
    constructor(hex: String) : this(rgb(hex))

    val rgb = rgb and 0xFFFFFF

    val red: Int get() = (rgb shr 16) and 0xFF
    val green: Int get() = (rgb shr 8) and 0xFF
    val blue: Int get() = (rgb shr 0) and 0xFF

    init {
        require(this.rgb in MIN_COLOR..MAX_COLOR) { "RGB should be in range of $MIN_COLOR..$MAX_COLOR but was ${this.rgb}" }
    }

    override fun toString(): String = "Color(red=$red,green=$green,blue=$blue)"

    override fun hashCode(): Int = rgb.hashCode()

    override fun equals(other: Any?): Boolean {
        val color = other as? Color ?: return false

        return color.rgb == rgb
    }

    companion object {
        private const val MIN_COLOR = 0
        private const val MAX_COLOR = 0xFFFFFF

        val White: Color get() = Color(255, 255, 255)
        val LightGray: Color get() = Color(192, 192, 192)
        val Gray: Color get() = Color(128, 128, 128)
        val DarkGray: Color get() = Color(64, 64, 64)
        val Black: Color get() = Color(1, 1, 1) //000 in Discord's colour system means transparent, workaround that

        val Red: Color get() = Color(255, 0, 0)
        val Orange: Color get() = Color(255, 175, 175)
        val Yellow: Color get() = Color(255, 255, 0)
        val Green: Color get() = Color(0, 255, 0)
        val Cyan: Color get() = Color(0, 255, 255)
        val Blue: Color get() = Color(0, 0, 255)
        val Pink: Color get() = Color(255, 175, 175)
        val Magenta: Color get() = Color(255, 0, 255)
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

private fun rgb(hex: String): Int {
    require(hex.startsWith("#")) { "Hex color must start with a '#'" }
    require(hex.length == 7) { "Hex color must be 7 characters long, including '#'" }


    return hex.removePrefix("#").toInt(16)
}

private fun rgb(red: Int, green: Int, blue: Int): Int {
    require(red in 0..255) { "Red should be in range of 0..255 but was $red" }
    require(green in 0..255) { "Green should be in range of 0..255 but was $green" }
    require(blue in 0..255) { "Blue should be in range of 0..255 but was $blue" }


    return red and 0xFF shl 16 or
            (green and 0xFF shl 8) or
            (blue and 0xFF) shl 0
}

val java.awt.Color.kColor get() = Color(rgb)
