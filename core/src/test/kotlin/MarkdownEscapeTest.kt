import dev.kord.core.parseMarkdown
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MarkdownEscapeTest {

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test escaping of italics`() {
        assertEquals("""\*italics\*""", escape("*italics*"), "Italics must be escaped (*)")
        assertEquals("""\_italics\_""", escape("_italics_"), "Italics must be escaped (_)")
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test escaping of links`() {
        assertEquals("""\[Google\]\(https://google.com\)""", escape("[Google](https://google.com)"))
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test escaping of underline`() {
        assertEquals(
            """\_\_\*underline italics\*\_\_""",
            escape("__*underline italics*__"),
            "Italics must be escaped when underlined"
        )

        assertEquals(
            """\_\_\*\*underline bold\*\*\_\_""",
            escape("__**underline bold**__"),
            "Bold must be escaped when underlined"
        )
        assertEquals(
            """\_\_\*\*\*underline bold italics\*\*\*\_\_""",
            escape("__***underline bold italics***__"),
            "Bold italics must be escaped when underlined"
        )
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test stripping of inline codeblock`() {
        assertEquals("""\`inline code block\`""", escape("`inline code block`"))
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test stripping of code fence`() {
        assertEquals("""\`\`\`code fence\`\`\`""", escape("```code fence```"))
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test stripping of code fence with language`() {
        assertEquals(
            """\`\`\`kotlin
            |val isCool = true
            |\`\`\`""".trimMargin(), escape(
                """```kotlin
            |val isCool = true
            |```""".trimMargin()
            )
        )
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    fun `test stripping of block quotes`() {
        assertEquals("""\> test 12346""", escape("> test 12346"))
    }

    private fun escape(text: String) = text.parseMarkdown().escape()
}