import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.guild.EmojiModifyBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EmojiModifyBuilderTest {

    @Test
    fun `builder does not create empty roles by default`() {
        val builder = EmojiModifyBuilder()

        val request = builder.toRequest()

        Assertions.assertEquals(Optional.Missing<Iterable<Snowflake>>(), request.roles)
    }

}