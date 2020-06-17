package com.gitlab.kordlib.common


class Color(val rgb: Int) {
    constructor(red: Int, green: Int, blue: Int) : this(rgb(red, green, blue))

    val red: Int get() = (rgb shr 16) and 0xFF
    val green: Int get() = (rgb shr 8) and 0xFF
    val blue: Int get() = (rgb shr 0) and 0xFF

    init {
        require(rgb in 0..0xFFFFFF) { "RGB should be in range of 0..16777215 but was $rgb" }
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

val java.awt.Color.kColor get() = Color(rgb)