package equality

import dev.kord.core.entity.KordEntity
import kotlin.test.Test
import kotlin.test.assertEquals

interface BehaviorEqualityTest<T: KordEntity> : EntityEqualityTest<T> {

    fun T.behavior() : KordEntity

    @Test
    fun `Full entity equals its behavior`(){
        val entity = newEntity(randomId())
        val behavior = entity.behavior()

        assertEquals(entity, behavior)
    }

    @Test
    fun `Behavior equals its full entity`(){
        val entity = newEntity(randomId())
        val behavior = entity.behavior()

        assertEquals(behavior, entity)
    }

}