import dev.kord.common.entity.Snowflake
import dev.kord.core.paginateBackwards
import dev.kord.core.paginateForwards
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

internal class UtilKtTest {

    @Test
    @JsName("test1")
    fun `paginate forwards selects the right id`() = runTest {

        val flow = paginateForwards(start = Snowflake(0u), batchSize = 100, idSelector = { it }) {
            var value = it.value.value
            if (value >= 1000u) return@paginateForwards emptyList<Snowflake>()
            value += 1u //don't include the position id

            buildList(100) {
                (value until (value + 100u)).reversed().forEach { snowflake -> //biggest/youngest -> smallest/oldest
                    add(Snowflake(snowflake))
                }
            }
        }

        assertEquals(1000, flow.count())
    }

    @Test
    @JsName("test2")
    fun `paginate backwards selects the right id`() = runTest {

        val flow = paginateBackwards(start = Snowflake(1000u), batchSize = 100, idSelector = { it }) {
            var value = it.value.value
            if (value <= 0u) return@paginateBackwards emptyList<Snowflake>()
            value -= 1u //don't include the position id

            buildList(100) {
                ((value - 99u /*reverse until, don't count the lowest value*/)..value).reversed().forEach { snowflake -> //biggest/youngest -> smallest/oldest
                    add(Snowflake(snowflake))
                }
            }
        }

        assertEquals(1000, flow.count())
    }

}
