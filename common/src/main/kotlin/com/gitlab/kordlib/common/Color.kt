package com.gitlab.kordlib.common


class Color(val rgb: Int) {
    constructor(red: Int, green: Int, blue: Int) : this(rgb(red, green, blue))

    val red: Int get() = (rgb shr 16) and 0xFF
    val green: Int get() = (rgb shr 8) and 0xFF
    val blue: Int get() = (rgb shr 0) and 0xFF

    init {
        if (rgb < 0 || rgb > 0xFFFFFF) throw IllegalArgumentException("Color is invalid.")
    }
}


private fun rgb(red: Int, green: Int, blue: Int): Int {
    return red and 0xFF shl 16 or
            (green and 0xFF shl 8) or
            (blue and 0xFF) shl 0
}

val java.awt.Color.kColor get() = Color(rgb)