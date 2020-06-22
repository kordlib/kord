import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfEnvironmentVariable(named = "TARGET_BRANCH", matches = "master")
class InviteTests {

    @Test
    fun `get invite from rest and cache`() = runBlocking {
        val kord = Kord(System.getenv("KORD_TEST_TOKEN"))
        val invite = kord.with(EntitySupplyStrategy.rest).getInvite("mpDQm5N")
        assertEquals("mpDQm5N", invite.code)
    }
}