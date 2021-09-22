package dev.kord.core.gateway.handler

import kotlinx.coroutines.Job

interface GatewayEventInterceptor {

    suspend fun start(): Job

}