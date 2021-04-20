import dev.kord.cache.api.put
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
    fun `rest only`() = runBlocking {
        kord.with(EntitySupplyStrategy.rest).getSelf()
        val inCache = kord.with(EntitySupplyStrategy.cache).getSelfOrNull()
        assertNull(inCache)
    }

    @Test
    @Disabled
    fun `cache only`() = runBlocking {
        val self = kord.with(EntitySupplyStrategy.rest).getSelf()
        kord.cache.put(self.data)

        val inCache = kord.with(EntitySupplyStrategy.cache).getSelf()
        assertEquals(self, inCache)
    }

    @Test
    fun `cache falls back to rest`() = runBlocking {
        val cache = kord.with(EntitySupplyStrategy.cache)
        val inCache = cache.getSelf()

        assertNull(inCache)

        val self = kord.getSelf()
        assertNotNull(self)
        kord.cache.put(self.data)

        assertEquals(self, cache.getSelf())

    }
}