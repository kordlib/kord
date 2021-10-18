@file:OptIn(KordVoice::class)

package dev.kord.voice.handlers

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.EncryptionMode
import dev.kord.voice.FrameInterceptorContextBuilder
import dev.kord.voice.VoiceConnection
import dev.kord.voice.gateway.*
import dev.kord.voice.udp.AudioFrameSenderConfiguration
import io.ktor.util.network.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val udpLifeCycleLogger = KotlinLogging.logger { }

@OptIn(KordVoice::class)
internal class UdpLifeCycleHandler(
    flow: Flow<VoiceEvent>,
    private val connection: VoiceConnection
) : ConnectionEventHandler<VoiceEvent>(flow, "UdpInterceptor") {
    private var ssrc: UInt? by atomic(null)
    private var server: NetworkAddress? by atomic(null)

    private var audioSenderJob: Job? by atomic(null)

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun start() = coroutineScope {
        on<Ready> {
            ssrc = it.ssrc
            server = NetworkAddress(it.ip, it.port)

            val ip: NetworkAddress = connection.socket.discoverIp(server!!, ssrc!!.toInt())

            udpLifeCycleLogger.trace { "ip discovered for voice successfully" }

            val selectProtocol = SelectProtocol(
                protocol = "udp",
                data = SelectProtocol.Data(
                    address = ip.hostname,
                    port = ip.port,
                    mode = EncryptionMode.XSalsa20Poly1305Lite
                )
            )

            connection.voiceGateway.send(selectProtocol)
        }

        on<SessionDescription> {
            with(connection) {
                val config = AudioFrameSenderConfiguration(
                    ssrc = ssrc!!,
                    key = it.secretKey.toUByteArray().toByteArray(),
                    provider = audioProvider,
                    baseFrameInterceptorContext = FrameInterceptorContextBuilder(gateway, voiceGateway),
                    interceptorFactory = frameInterceptorFactory,
                    server = server!!
                )

                audioSenderJob?.cancel()
                audioSenderJob = launch { frameSender.start(config) }
            }
        }

        on<Close> {
            audioSenderJob?.cancel()
            audioSenderJob = null
        }
    }
}