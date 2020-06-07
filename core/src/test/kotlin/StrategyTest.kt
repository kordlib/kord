import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.core.EntitySupplyStrategy
import com.gitlab.kordlib.core.Kord
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
class StrategyTest {


    lateinit var kord: Kord

    @BeforeAll
    fun setup() = runBlocking {
        kord = Kord(System.getenv("token"))
    }

    @Test
    fun `rest only`() = runBlocking {
        kord.with(EntitySupplyStrategy.Rest).getSelf()
        val incache = kord.cache.getSelf()
        assertNull(incache)
    }

    @Test
    @Disabled
    fun `cache only`() = runBlocking {
        val self = kord.with(EntitySupplyStrategy.Rest).getSelf()
        kord.cache.put(self!!.data)

        val incache = kord.with(EntitySupplyStrategy.Cache).getSelf()
        assertEquals(self, incache)
    }

    @Test
    fun `cache fallsback to rest`() = runBlocking {
        val cache = kord.with(EntitySupplyStrategy.Cache)
        val incache = cache.getSelf()

        assertNull(incache)

        val self = kord.getSelf()
        assertNotNull(self)
        kord.cache.put(self.data)

        assertEquals(self, cache.getSelf())

    }
}