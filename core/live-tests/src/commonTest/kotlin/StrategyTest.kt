package dev.kord.core

import dev.kord.cache.api.put
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StrategyTest {

    @Test
    @JsName("test1")
    fun `rest only`() = runTest {
        withKord { kord ->
            val fromRest = kord.with(EntitySupplyStrategy.rest).getSelfOrNull()
            val inCache = kord.with(EntitySupplyStrategy.cache).getSelfOrNull()
            assertNull(inCache)
            assertNotNull(fromRest)
        }
    }

    @Test
    @JsName("test2")
    fun `cache only`() = runTest {
        withKord { kord ->
            kord.cache.put(kord.getSelf().data)
            val inCache = kord.with(EntitySupplyStrategy.cache).getSelfOrNull()
            assertNotNull(inCache)
        }
    }

    @Test
    @JsName("test3")
    fun `cache falls back to rest`() = runTest {
        withKord { kord ->
            val cache = kord.with(EntitySupplyStrategy.cache)
            val inCache = cache.getSelfOrNull()

            assertNull(inCache)

            val self = kord.getSelf()
            assertNotNull(self)
            kord.cache.put(self.data)

            assertEquals(self, cache.getSelf())

        }
    }
}
