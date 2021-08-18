package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

/**
 * An implementation of the Discord [Gateway](https://discord.com/developers/docs/topics/voice-connections) (see gateway-only, not UDP connection in docs) and its lifecycle.
 *
 * Allows consumers to receive [Event]s through [events] and send [Command]s through [send].
 */
@KordVoice
interface VoiceGateway : CoroutineScope {
    /**
     * The incoming [Event]s of the Gateway. Users should expect [kotlinx.coroutines.flow.Flow]s to be hot and remain
     * open for the entire lifecycle of the Gateway.
     */
    val events: SharedFlow<Event>

    /**
     * The [Duration] between the last [Heartbeat] and [HeartbeatAck].
     *
     * This flow will have a [value][StateFlow.value] off `null` if the gateway is not [active][VoiceGateway.start],
     * or no [HeartbeatAck] has been received yet.
     */
    val ping: StateFlow<Duration?>

    /**
     * Starts a reconnection voice gateway connection with the given [configuration]. This function will suspend
     * until the lifecycle of the gateway has ended.
     *
     * @param configuration - the configuration for this gateway session.
     */
    suspend fun start(configuration: VoiceGatewayConfiguration)

    /**
     * Sends a [Command] to the gateway, suspending until the message has been sent.
     *
     * @param command the [Command] to send to the gateway.
     * @throws Exception when the gateway isn't open/
     */
    suspend fun send(command: Command)

    /**
     * Close the Gateway and ends the current session, suspending until the underlying websocket is closed.
     */
    suspend fun stop()

    companion object {
        private object None : VoiceGateway {
            override val coroutineContext: CoroutineContext =
                SupervisorJob() + EmptyCoroutineContext + CoroutineName("None Voice Gateway")

            override val events: SharedFlow<Event>
                get() = MutableSharedFlow()

            override val ping: StateFlow<Duration?>
                get() = MutableStateFlow(null)

            override suspend fun send(command: Command) {}

            override suspend fun start(configuration: VoiceGatewayConfiguration) {}

            override suspend fun stop() {}

            override fun toString(): String {
                return "Gateway.None"
            }
        }

        /**
         * Returns a [VoiceGateway] with no-op behavior, an empty [VoiceGateway.events] flow and a ping of [Duration.ZERO].
         */
        fun none(): VoiceGateway = None
    }
}

/**
 * Representation of Discord's [Voice Gateway close codes](https://discord.com/developers/docs/topics/opcodes-and-status-codes#voice-voice-close-event-codes).
 */
sealed class VoiceGatewayCloseCode(val code: Int) {
    class Unknown(code: Int) : VoiceGatewayCloseCode(code)
    object UnknownOpcode : VoiceGatewayCloseCode(4001)
    object FailedToDecodePayload : VoiceGatewayCloseCode(4002)
    object NotAuthenticated : VoiceGatewayCloseCode(4003)
    object AuthenticationFailed : VoiceGatewayCloseCode(4004)
    object AlreadyAuthenticated : VoiceGatewayCloseCode(4005)
    object SessionNoLongerValid : VoiceGatewayCloseCode(4006)
    object SessionTimeout : VoiceGatewayCloseCode(4009)
    object ServerNotFound : VoiceGatewayCloseCode(4011)
    object UnknownProtocol : VoiceGatewayCloseCode(4012)
    object Disconnect : VoiceGatewayCloseCode(4014)
    object VoiceServerCrashed : VoiceGatewayCloseCode(4015)
    object UnknownEncryptionMode : VoiceGatewayCloseCode(4016);

    companion object {
        fun of(code: Int) =
            when (code) {
                4001 -> UnknownOpcode
                4002 -> FailedToDecodePayload
                4003 -> NotAuthenticated
                4004 -> AuthenticationFailed
                4005 -> AlreadyAuthenticated
                4006 -> SessionNoLongerValid
                4009 -> SessionTimeout
                4011 -> ServerNotFound
                4012 -> UnknownProtocol
                4014 -> Disconnect
                4015 -> VoiceServerCrashed
                4016 -> UnknownEncryptionMode
                else -> Unknown(code)
            }
    }
}