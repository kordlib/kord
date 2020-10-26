package com.gitlab.kordlib.core.supplier

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.ClientResources
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.KordCacheBuilder
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.gateway.Intents
import com.gitlab.kordlib.gateway.PrivilegedIntent
import com.gitlab.kordlib.rest.request.KtorRequestHandler
import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class CacheEntitySupplierTest {

    @Test
    @OptIn(PrivilegedIntent::class)
    fun `cache does not throw when accessing unregistered entities`(): Unit = runBlocking {
        val kord = Kord(
                ClientResources("", 0, HttpClient(), EntitySupplyStrategy.cache, Intents.all),
                KordCacheBuilder().build(),
                MasterGateway(mapOf(0 to Gateway.none())),
                RestClient(KtorRequestHandler("")),
                Snowflake(0),
                BroadcastChannel(1),
                Dispatchers.Default
        )

        kord.unsafe.guild(Snowflake(0)).regions.toList()
    }

}