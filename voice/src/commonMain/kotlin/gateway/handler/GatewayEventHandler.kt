package dev.kord.voice.gateway.handler

import dev.kord.voice.gateway.VoiceEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging

private val logger = KotlinLogging.logger("[Handler]")

internal abstract class GatewayEventHandler(
    val flow: Flow<VoiceEvent>,
    val name: String
) {
    open suspend fun start() {}

    suspend inline fun <reified T> CoroutineScope.on(crossinline block: suspend (T) -> Unit) {
        flow.filterIsInstance<T>().onEach {
            try {
                block(it)
            } catch (exception: Exception) {
                logger.error(exception) { "[$name]" }
            }
        }.launchIn(this)
    }
}