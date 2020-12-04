package dev.kord.core

import dev.kord.common.entity.Snowflake
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class UtilKtTest {

    @Test
    @ExperimentalStdlibApi
    fun `paginate forwards selects the right id`() = runBlockingTest {

        val flow = paginateForwards(start = Snowflake(0), batchSize = 100, idSelector = { it }) {
            var value = it.value.value
            if (value >= 1000) return@paginateForwards emptyList<Snowflake>()
            value += 1 //don't include the position id

            buildList(100) {
                (value until (value + 100)).reversed().forEach { snowflake -> //biggest/youngest -> smallest/oldest
                    add(Snowflake(snowflake))
                }
            }
        }

        Assertions.assertEquals(1000, flow.count())
    }

    @Test
    @ExperimentalStdlibApi
    fun `paginate backwards selects the right id`() = runBlockingTest {

        val flow = paginateBackwards(start = Snowflake(1000), batchSize = 100, idSelector = { it }) {
            var value = it.value.value
            if (value <= 0) return@paginateBackwards emptyList<Snowflake>()
            value -= 1 //don't include the position id

            buildList(100) {
                ((value - 99 /*reverse until, don't count the lowest value*/)..value).reversed().forEach { snowflake -> //biggest/youngest -> smallest/oldest
                    add(Snowflake(snowflake))
                }
            }
        }

        Assertions.assertEquals(1000, flow.count())
    }

}
