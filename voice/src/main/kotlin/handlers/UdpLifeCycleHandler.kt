@file:OptIn(KordVoice::class)

package dev.kord.voice.handlers

import dev.kord.common.annotation.KordVoice
import dev.kord.voice.EncryptionMode
import dev.kord.voice.FrameInterceptorContextBuilder
import dev.kord.voice.VoiceConnection
import dev.kord.voice.gateway.Ready
import dev.kord.voice.gateway.SelectProtocol
import dev.kord.voice.gateway.SessionDescription
import dev.kord.voice.gateway.VoiceEvent
import dev.kord.voice.udp.AudioFrameSenderConfiguration
import io.ktor.util.network.*
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging

private val udpLifeCycleLogger = KotlinLogging.logger { }

@OptIn(KordVoice::class)
internal class UdpLifeCycleHandler(
    flow: Flow<VoiceEvent>,
    private val connection: VoiceConnection
) : EventHandler<VoiceEvent>(flow, "UdpInterceptor") {
    private val ssrc: AtomicRef<UInt?> = atomic(null)
    private val server: AtomicRef<NetworkAddress?> = atomic(null)

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun start() {
        on<Ready> {
            this.ssrc.value = it.ssrc
            this.server.value = NetworkAddress(it.ip, it.port)

            val ip: NetworkAddress = connection.socket.discoverIp(this.server.value!!, this.ssrc.value!!.toInt())

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
                    ssrc = ssrc.value!!,
                    key = it.secretKey.toUByteArray().toByteArray(),
                    provider = audioProvider,
                    baseFrameInterceptorContext = FrameInterceptorContextBuilder(gateway, voiceGateway),
                    interceptorFactory = frameInterceptorFactory,
                    server = server.value!!
                )

                frameSender.start(config)
            }
        }
    }
}