import dev.kord.core.Kord
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import io.mockk.every
import io.mockk.mockk

fun mockKord(): Kord =
        mockk {
            val supplier = mockk<EntitySupplier>()

            val strategy = mockk<EntitySupplyStrategy<*>> {
                every { supply(any()) } returns supplier
            }

            every { resources } returns mockk {
                every { defaultStrategy } returns strategy
            }
            every { defaultSupplier } returns supplier
        }