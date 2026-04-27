package dev.kord.voice.handlers

import dev.kord.voice.EncryptionMode
import dev.kord.voice.FrameInterceptorConfiguration
import dev.kord.voice.VoiceConnection
import dev.kord.voice.gateway.*
import dev.kord.voice.udp.AudioFrameSenderConfiguration
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.sockets.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private val udpLifeCycleLogger = KotlinLogging.logger { }

internal class UdpLifeCycleHandler(
    flow: Flow<VoiceEvent>,
    private val connection: VoiceConnection
) : ConnectionEventHandler<VoiceEvent>(flow, "Udp Handler") {
    private var ssrc: UInt? by atomic(null)
    private var server: InetSocketAddress? by atomic(null)

    private var audioSenderJob: Job? by atomic(null)

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun start() = coroutineScope {
        on<Ready> {
            ssrc = it.ssrc
            server = InetSocketAddress(it.ip, it.port)

            // Initialize DAVE protocol FIRST — before any network I/O.
            // Discord sends SessionDescription, ExternalSenderPackage, and Proposals
            // immediately after SelectProtocol. The DAVE session must already exist
            // so DaveProtocolHandler can process those events with an active session.
            if (connection.daveProtocol.maxProtocolVersion > 0) {
                connection.daveProtocol.initialize(
                    version = connection.daveProtocol.maxProtocolVersion,
                    channelId = connection.data.channelId.value.toString(),
                    selfUserId = connection.data.selfId.value.toLong(),
                    authSessionId = it.authSessionId
                )
                udpLifeCycleLogger.debug { "DAVE: session initialized before IP discovery" }
            }

            val ip: InetSocketAddress = connection.socket.discoverIp(server!!, ssrc!!.toInt())

            udpLifeCycleLogger.trace { "ip discovered for voice successfully" }

            // Select best encryption mode: prefer AES-GCM, then XChaCha20
            val encryptionMode = selectBestMode(it.modes)

            val selectProtocol = SelectProtocol(
                protocol = "udp",
                data = SelectProtocol.Data(
                    address = ip.hostname,
                    port = ip.port,
                    mode = encryptionMode
                )
            )

            connection.voiceGateway.send(selectProtocol)
        }

        on<SessionDescription> {
            val selectedMode = it.mode

            with(connection) {
                // Set up DAVE SSRC codec mapping
                daveProtocol.assignSsrcToCodec(ssrc!!)

                // Send DAVE key package if DAVE is enabled
                if (it.daveProtocolVersion > 0) {
                    daveProtocol.setProtocolVersion(it.daveProtocolVersion)
                    val keyPackage = daveProtocol.getMarshalledKeyPackage()
                    if (keyPackage.isNotEmpty()) {
                        voiceGateway.sendBinary(OpCode.DaveMlsKeyPackage.code, keyPackage)
                        udpLifeCycleLogger.debug { "DAVE: sent initial key package (${keyPackage.size} bytes)" }
                    }
                }

                val config = AudioFrameSenderConfiguration(
                    ssrc = ssrc!!,
                    key = it.secretKey.toUByteArray().toByteArray(),
                    server = server!!,
                    interceptorConfiguration = FrameInterceptorConfiguration(gateway, voiceGateway, ssrc!!),
                    encryptionMode = selectedMode,
                    daveProtocol = daveProtocol
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

    private fun selectBestMode(serverModes: List<EncryptionMode>): EncryptionMode {
        if (EncryptionMode.AeadAes256GcmRtpSize in serverModes) return EncryptionMode.AeadAes256GcmRtpSize
        return EncryptionMode.AeadXChaCha20Poly1305RtpSize
    }
}
