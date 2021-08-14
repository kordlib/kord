package dev.kord.voice.handlers

import dev.kord.voice.EncryptionMode
import dev.kord.voice.gateway.*
import dev.kord.voice.udp.AudioFramePollerConfigurationBuilder
import dev.kord.voice.udp.DiscordUdpConnection
import dev.kord.voice.udp.VoiceUdpConnection
import io.ktor.util.network.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import dev.kord.voice.gateway.Event as VoiceEvent

private val udpLifeCycleLogger = KotlinLogging.logger { }

internal class UdpLifeCycleHandler(
    flow: Flow<VoiceEvent>,
    private val send: suspend (Command) -> Unit,
    private val poll: (AudioFramePollerConfigurationBuilder) -> Unit,
    private val udpDispatcher: CoroutineDispatcher = Dispatchers.Default
) : EventHandler<VoiceEvent>(flow, "UdpInterceptor") {
    private var framePollerConfigurationBuilder = AudioFramePollerConfigurationBuilder()
    private var udp: DiscordUdpConnection? = null

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun start() {
        on<Ready> {
            framePollerConfigurationBuilder = AudioFramePollerConfigurationBuilder() // new config

            framePollerConfigurationBuilder.ssrc = it.ssrc

            val udpConnection = VoiceUdpConnection(
                server = NetworkAddress(it.ip, it.port),
                ssrc = it.ssrc,
                dispatcher = udpDispatcher
            )

            udp = udpConnection
            framePollerConfigurationBuilder.udp = udpConnection

            val ip: NetworkAddress = udpConnection.performIpDiscovery()

            udpLifeCycleLogger.trace { "ip discovered for voice: $ip" }

            val selectProtocol = SelectProtocol(
                protocol = "udp",
                data = SelectProtocol.Data(
                    address = ip.hostname,
                    port = ip.port,
                    mode = EncryptionMode.XSalsa20Poly1305
                )
            )

            send(selectProtocol)
        }

        on<SessionDescription> {
            framePollerConfigurationBuilder.key = it.secretKey.toUByteArray().toByteArray().toList()

            poll(framePollerConfigurationBuilder) // we can start polling at this point
        }

        on<Close> {
            udp?.close()
        }
    }
}