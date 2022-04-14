package dev.kord.voice.gateway

import dev.kord.common.annotation.KordVoice
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import mu.KLogger
import mu.KotlinLogging
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

/**
 * An implementation of the Discord [Gateway](https://discord.com/developers/docs/topics/voice-connections) (see gateway-only, not UDP connection in docs) and its lifecycle.
 *
 * Allows consumers to receive [VoiceEvent]s through [events] and send [Command]s through [send].
 */
@KordVoice
public interface VoiceGateway {
    public val scope: CoroutineScope

    /**
     * The incoming [VoiceEvent]s of the Gateway. Users should expect [kotlinx.coroutines.flow.Flow]s to be hot and remain
     * open for the entire lifecycle of the Gateway.
     */
    public val events: SharedFlow<VoiceEvent>

    /**
     * The [Duration] between the last [Heartbeat] and [HeartbeatAck].
     *
     * This flow will have a [value][StateFlow.value] off `null` if the gateway is not [active][VoiceGateway.start],
     * or no [HeartbeatAck] has been received yet.
     */
    public val ping: StateFlow<Duration?>

    /**
     * Starts a reconnection voice gateway connection with the given [configuration]. This function will suspend
     * until the lifecycle of the gateway has ended.
     *
     * @param configuration - the configuration for this gateway session.
     */
    public suspend fun start(configuration: VoiceGatewayConfiguration)

    /**
     * Sends a [Command] to the gateway, suspending until the message has been sent.
     *
     * @param command the [Command] to send to the gateway.
     * @throws Exception when the gateway isn't open/
     */
    public suspend fun send(command: Command)

    /**
     * Close the Gateway and ends the current session, suspending until the underlying websocket is closed.
     */
    public suspend fun stop()

    public companion object {
        private object None : VoiceGateway {
            override val scope: CoroutineScope =
                CoroutineScope(EmptyCoroutineContext + CoroutineName("None VoiceGateway"))

            override val events: SharedFlow<VoiceEvent>
                get() = MutableSharedFlow()

            override val ping: StateFlow<Duration?>
                get() = MutableStateFlow(null)

            override suspend fun send(command: Command) {}

            override suspend fun start(configuration: VoiceGatewayConfiguration) {}

            override suspend fun stop() {}

            override suspend fun detach() {}

            override fun toString(): String {
                return "Gateway.None"
            }
        }

        /**
         * Returns a [VoiceGateway] with no-op behavior, an empty [VoiceGateway.events] flow and a ping of [Duration.ZERO].
         */
        public fun none(): VoiceGateway = None
    }

    public suspend fun detach()
}


/**
 * Logger used to report throwables caught in [VoiceGateway.on].
 */
@PublishedApi
internal val voiceGatewayOnLogger: KLogger = KotlinLogging.logger("Gateway.on")

/**
 * Convenience method that will invoke the [consumer] on every event [T] created by [VoiceGateway.events].
 *
 * The events are buffered in an [unlimited][Channel.UNLIMITED] [buffer][Flow.buffer] and
 * [launched][CoroutineScope.launch] in the supplied [scope], which is [VoiceGateway] by default.
 * Each event will be [launched][CoroutineScope.launch] inside the [scope] separately and
 * any thrown [Throwable] will be caught and logged.
 *
 * The returned [Job] is a reference to the created coroutine, call [Job.cancel] to cancel the processing of any further
 * events for this [consumer].
 */
@KordVoice
public inline fun <reified T : VoiceEvent> VoiceGateway.on(
    scope: CoroutineScope = this.scope,
    crossinline consumer: suspend T.() -> Unit
): Job {
    return this.events.buffer(Channel.UNLIMITED).filterIsInstance<T>().onEach {
        scope.launch { it.runCatching { it.consumer() }.onFailure(voiceGatewayOnLogger::error) }
    }.launchIn(scope)
}

/**
 * Representation of Discord's [Voice Gateway close codes](https://discord.com/developers/docs/topics/opcodes-and-status-codes#voice-voice-close-event-codes).
 */
public sealed class VoiceGatewayCloseCode(public val code: Int) {
    public class Unknown(code: Int) : VoiceGatewayCloseCode(code)
    public object UnknownOpcode : VoiceGatewayCloseCode(4001)
    public object FailedToDecodePayload : VoiceGatewayCloseCode(4002)
    public object NotAuthenticated : VoiceGatewayCloseCode(4003)
    public object AuthenticationFailed : VoiceGatewayCloseCode(4004)
    public object AlreadyAuthenticated : VoiceGatewayCloseCode(4005)
    public object SessionNoLongerValid : VoiceGatewayCloseCode(4006)
    public object SessionTimeout : VoiceGatewayCloseCode(4009)
    public object ServerNotFound : VoiceGatewayCloseCode(4011)
    public object UnknownProtocol : VoiceGatewayCloseCode(4012)
    public object Disconnect : VoiceGatewayCloseCode(4014)
    public object VoiceServerCrashed : VoiceGatewayCloseCode(4015)
    public object UnknownEncryptionMode : VoiceGatewayCloseCode(4016)

    public companion object {
        public fun of(code: Int): VoiceGatewayCloseCode =
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
