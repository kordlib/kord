import dev.kord.cache.api.put
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class StrategyTest {

    lateinit var kord: Kord

    @BeforeAll
    fun setup() = runBlocking {
        kord = Kord(System.getenv("KORD_TEST_TOKEN"))
    }

    @Test
    @Order(1)
    fun `rest only`() = runBlocking {
        val fromRest = kord.with(EntitySupplyStrategy.rest).getSelfOrNull()
        val inCache = kord.with(EntitySupplyStrategy.cache).getSelfOrNull()
        assertNull(inCache)
        assertNotNull(fromRest)
    }

    @Test
    @Order(3)
    fun `cache only`() = runBlocking {

        val inCache = kord.with(EntitySupplyStrategy.cache).getSelfOrNull()
        assertNotNull(inCache)
    }

    @Test
    @Order(2)
    fun `cache falls back to rest`() = runBlocking {
        val cache = kord.with(EntitySupplyStrategy.cache)
        val inCache = cache.getSelfOrNull()

        assertNull(inCache)

        val self = kord.getSelf()
        assertNotNull(self)
        kord.cache.put(self.data)

        assertEquals(self, cache.getSelf())

    }
}