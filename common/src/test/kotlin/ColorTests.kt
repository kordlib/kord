import dev.kord.common.Color
import dev.kord.common.kColor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.DisabledIfSystemProperty
import kotlin.test.assertEquals

class ColorTests {
    @Test
    fun `Color throws if invalid rgb value is provided`() {
        assertThrows<IllegalArgumentException> { Color(256, 256, 300) }
    }

    @Test
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

    // Currently java.awt.Color doesn't work on Graal native images
    // See: https://github.com/oracle/graal/issues/3084
    @DisabledIfSystemProperty(named = "org.graalvm.nativeimage.imagecode", matches = ".+")
    @Test
    fun `java to kColor conversion`() {
        if(!System.getProperty("").isNullOrBlank()) return

        val color = java.awt.Color.decode("#DBD0B4").kColor

        assertEquals(219, color.red)
        assertEquals(208, color.green)
        assertEquals(180, color.blue)
    }

    @Test
    fun `Color implementation should drop alpha values if given`() {
        val color = Color(0x1E1F2E3D)
        assertEquals(0x1F2E3D, color.rgb)
    }
}
