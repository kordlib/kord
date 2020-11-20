import com.gitlab.kordlib.common.Color
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ColorTests {
    @Test
    fun `Color throws if invalid rgb value is provided`() {
        assertThrows<IllegalArgumentException> { Color(-1) }
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
}