import dev.kord.common.kColor
import kotlin.test.assertEquals

import kotlin.test.Test

class ColorTests {
    @Test
    fun `java to kColor conversion`() {
        val color = java.awt.Color.decode("#DBD0B4").kColor

        assertEquals(219, color.red)
        assertEquals(208, color.green)
        assertEquals(180, color.blue)
    }
}
