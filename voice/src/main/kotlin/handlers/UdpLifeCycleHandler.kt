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
import dev.kord.voice.udp.VoiceUdpConnectionConfiguration
import io.ktor.util.network.*
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.Delegates

private val udpLifeCycleLogger = KotlinLogging.logger { }

@OptIn(KordVoice::class)
internal class UdpLifeCycleHandler(
    flow: Flow<VoiceEvent>,
    private val connection: VoiceConnection
) : EventHandler<VoiceEvent>(flow, "UdpInterceptor") {
    private val ssrc: AtomicReference<UInt> = AtomicReference()

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun start() {
        on<Ready> {
            this.ssrc.set(it.ssrc)

            connection.udp.start(VoiceUdpConnectionConfiguration(NetworkAddress(it.ip, it.port), it.ssrc))

            val ip: NetworkAddress = connection.udp.discoverIp()

            udpLifeCycleLogger.trace { "ip discovered for voice successfully" }

            val selectProtocol = SelectProtocol(
                protocol = "udp",
                data = SelectProtocol.Data(
                    address = ip.hostname,
                    port = ip.port,
                    mode = EncryptionMode.XSalsa20Poly1305
                )
            )

            connection.voiceGateway.send(selectProtocol)
        }

        on<SessionDescription> {
            with(connection) {
                val config = AudioFrameSenderConfiguration(
                    ssrc = ssrc.get(),
                    key = it.secretKey.toUByteArray().toByteArray(),
                    provider = audioProvider,
                    baseFrameInterceptorContext = FrameInterceptorContextBuilder(gateway, voiceGateway),
                    interceptorFactory = frameInterceptorFactory
                )

                frameSender.start(config)
            }
        }
    }
}