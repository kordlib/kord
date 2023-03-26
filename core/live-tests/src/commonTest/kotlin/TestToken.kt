import dev.kord.cache.map.MapDataCache
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.builder.kord.configure
import dev.kord.core.builder.kord.getBotIdFromToken
import dev.kord.core.cache.registerKordData
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.builder.Shards
import dev.kord.rest.service.RestClient
import dev.kord.test.getEnv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import regression.CrashingHandler
import regression.FakeGateway

val testToken = getEnv("KORD_TEST_TOKEN") ?: error("KORD_TEST_TOKEN is not defined")

suspend inline fun withKord(block: (kord: Kord) -> Unit) {
    val token = testToken
    val resources = ClientResources(
        token,
        getBotIdFromToken(token),
        Shards(1),
        maxConcurrency = 1,
        null.configure(),
        EntitySupplyStrategy.cacheWithRestFallback,
    )
    val kord = Kord(
        resources,
        MapDataCache().also { it.registerKordData() },
        DefaultMasterGateway(mapOf(0 to FakeGateway)),
        RestClient(CrashingHandler(resources.httpClient, resources.token)),
        getBotIdFromToken(token),
        MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE),
        Dispatchers.Default,
        DefaultGatewayEventInterceptor(),
    )
    block(kord)
    kord.shutdown()
}
