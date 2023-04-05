import dev.kord.core.parseMarkdown
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MarkdownStripTest {

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test escaping of italics`() {
        assertEquals("""italics""", strip("*italics*"), "Italics must be stripped (*)")
        assertEquals("""italics""", strip("_italics_"), "Italics must be stripped (_)")
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test escaping of underline`() {
        assertEquals(
            """underline italics""",
            strip("underline italics"),
            "Italics must be stripeed when underlined"
        )

        assertEquals(
            """underline bold""",
            strip("__**underline bold**__"),
            "Bold must be stripeed when underlined"
        )
        assertEquals(
            """underline bold italics""",
            strip("__***underline bold italics***__"),
            "Bold italics must be stripeed when underlined"
        )
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test stripping of inline codeblock`() {
        assertEquals("inline code block", strip("`inline code block`"))
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test stripping of code fence`() {
        assertEquals("code fence", strip("```code fence```"))
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test stripping of links`() {
        assertEquals("Google", strip("[Google](https://google.com)"))
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test stripping of code fence with language`() {
        assertEquals(
            """
                |
                |val isCool = true
                |""".trimMargin(), strip(
                """```kotlin
            |val isCool = true
            |```""".trimMargin()
            )
        )
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test stripping of block quotes`() {
        assertEquals("test 12346", strip("> test 12346"))
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `do not strip escaped characters`() {
        assertEquals("""\*\*test123465\*\*""", strip("\\*\\*test123465\\*\\*"))
    }

    private fun strip(text: String) = text.parseMarkdown().strip()
}