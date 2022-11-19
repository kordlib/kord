package supplier

import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.Snowflake
import dev.kord.core.ClientResources
import dev.kord.core.Kord
import dev.kord.core.cache.KordCacheBuilder
import dev.kord.core.gateway.DefaultMasterGateway
import dev.kord.core.gateway.handler.DefaultGatewayEventInterceptor
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Gateway
import dev.kord.gateway.builder.Shards
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class CacheEntitySupplierTest {

    @Test
    @OptIn(KordUnsafe::class)
    fun `cache does not throw when accessing unregistered entities`(): Unit = runBlocking {
        val kord = Kord(
            ClientResources("", Snowflake(0u), Shards(0), maxConcurrency = 1, HttpClient(), EntitySupplyStrategy.cache),
            KordCacheBuilder().build(),
            DefaultMasterGateway(mapOf(0 to Gateway.none())),
            RestClient(KtorRequestHandler("")),
            Snowflake(0u),
            MutableSharedFlow(),
            Dispatchers.Default,
            DefaultGatewayEventInterceptor(),
        )

        kord.unsafe.guild(Snowflake(0u)).regions.toList()
    }

}
