import dev.kord.cache.api.put
import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.*

class StrategyTest {

    lateinit var kord: Kord

    @BeforeTest
    fun setup() = runTest {
        kord = Kord(testToken)
    }

    @Test
    @JsName("test1")
    fun `rest only`() = runTest {
        val fromRest = kord.with(EntitySupplyStrategy.rest).getSelfOrNull()
        val inCache = kord.with(EntitySupplyStrategy.cache).getSelfOrNull()
        assertNull(inCache)
        assertNotNull(fromRest)
    }

    @Test
    @JsName("test2")
    fun `cache only`() = runTest {

        val inCache = kord.with(EntitySupplyStrategy.cache).getSelfOrNull()
        assertNotNull(inCache)
    }

    @Test
    @JsName("test3")
    fun `cache falls back to rest`() = runTest {
        val cache = kord.with(EntitySupplyStrategy.cache)
        val inCache = cache.getSelfOrNull()

        assertNull(inCache)

        val self = kord.getSelf()
        assertNotNull(self)
        kord.cache.put(self.data)

        assertEquals(self, cache.getSelf())

    }
}
