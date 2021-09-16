package gateway

import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.gateway.Command
import dev.kord.gateway.Event
import dev.kord.gateway.Gateway
import dev.kord.gateway.GatewayConfiguration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

internal class DefaultMasterGatewayTest {

    @Test
    fun `Gateway takes ping of single child`(){
        val dummy = DummyGateway()
        val ping = Duration.milliseconds(150)

        dummy.ping.value = ping

        val gateway = DefaultMasterGateway(
            mapOf(0 to dummy)
        )

        assertEquals(dummy.ping.value, gateway.averagePing)
    }

    @Test
    fun `Gateway takes ping average of multiple children`(){
        val dummy1 = DummyGateway()
        val dummy2 = DummyGateway()
        val ping1 = Duration.milliseconds(100)
        val ping2 = Duration.milliseconds(200)

        dummy1.ping.value = ping1
        dummy2.ping.value = ping2

        val gateway = DefaultMasterGateway(
            mapOf(0 to dummy1, 1 to dummy2)
        )

        assertEquals(Duration.milliseconds(150), gateway.averagePing)
    }

    @Test
    fun `Gateway returns null ping when no gateway pings`(){
        val dummy = DummyGateway()

        val gateway = DefaultMasterGateway(
            mapOf(0 to dummy)
        )

        assertEquals(null, gateway.averagePing)
    }


    private class DummyGateway : Gateway {
        override val events: SharedFlow<Event>
            get() = MutableSharedFlow()

        override val ping: MutableStateFlow<Duration?> = MutableStateFlow(null)

        override val coroutineContext: CoroutineContext
            get() = EmptyCoroutineContext

        override suspend fun detach() {}

        override suspend fun send(command: Command) {}

        override suspend fun start(configuration: GatewayConfiguration) {}

        override suspend fun stop() {}

    }

}
