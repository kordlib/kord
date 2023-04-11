package dev.kord.common

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.condition.DisabledInNativeImage

class ColorTests {
    @Test
    @JsName("test1")
    fun `Color throws if invalid rgb value is provided`() {
        assertFailsWith<IllegalArgumentException> { Color(256, 256, 300) }
    }

    @Test
    @JsName("test2")
    fun `Color provides a correct value`() {
        val red = Color(0xFF0000)
        assertEquals(255, red.red)
        assertEquals(0, red.green)
        assertEquals(0, red.blue)

        val white = Color(255, 255, 255)
        assertEquals(255, white.red)
        assertEquals(255, white.green)
        assertEquals(255, white.blue)
        assertEquals(0xFFFFFF, white.rgb)
    }

    // Currently java.awt.Color doesn't work on GraalVM Native Image
    // See: https://github.com/oracle/graal/issues/3084
    @DisabledInNativeImage
    @Test
    @JsName("test3")
    fun `Color implementation should drop alpha values if given`() {
        val color = Color(0x1E1F2E3D)
        assertEquals(0x1F2E3D, color.rgb)
    }
}
