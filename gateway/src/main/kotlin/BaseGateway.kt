package dev.kord.gateway

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import mu.KLogger
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

/**
 * Base abstraction for a gateway implementation.
 */
public abstract class BaseGateway : Gateway {

    /**
     * The logger for this gateway.
     */
    protected val log: KLogger = KotlinLogging.logger { }

    /**
     * The current state of this gateway as atomic reference.
     * @see State
     */
    private val atomicState: AtomicRef<State> = atomic(State.Stopped)

    /**
     * The current state of this gateway.
     * To change it use the [atomicState] reference.
     * @see State
     */
    protected var state: State by atomicState

    /**
     * The dispatcher used to run the gateway.
     * It will be used to assemble the [coroutineContext] of this gateway.
     * By default, there is [SupervisorJob] before the dispatcher in [coroutineContext].
     */
    protected abstract val dispatcher: CoroutineDispatcher

    override val coroutineContext: CoroutineContext get() = SupervisorJob() + dispatcher

    override val events: MutableSharedFlow<Event> = MutableSharedFlow()

    override suspend fun start(configuration: GatewayConfiguration) {
        requireState<State.Stopped>()
        atomicState.update { State.Running(true) }
        onStart(configuration)
    }

    /**
     * This method is called just after the [start] method,
     * once the state is updated to [State.Running].
     * The state is ensured to be valid before this method is called.
     */
    protected abstract suspend fun onStart(configuration: GatewayConfiguration)

    override suspend fun stop() {
        requireStateIsNot<State.Detached>()
        events.emit(Close.UserClose)
        atomicState.update { State.Stopped }
        onStop()
    }

    /**
     * This method is called just after the [stop] method,
     * once the [Close.UserClose] event is emitted,
     * and the state is updated to [State.Stopped].
     * The state is ensured to be valid before this method is called.
     */
    protected abstract suspend fun onStop()

    override suspend fun detach() {
        (this as CoroutineScope).cancel()
        if (state is State.Detached) return
        atomicState.update { State.Detached }
        events.emit(Close.Detach)
        onDetach()
    }

    /**
     * This method is called just after the [detach] method,
     * once the state is updated to [State.Detached],
     * and the [Close.Detach] event is emitted.
     * The state is ensured to be valid before this method is called.
     */
    protected abstract suspend fun onDetach()

    override suspend fun send(command: Command) {
        requireStateIsNot<State.Detached>()
        onSend(command)
    }

    /**
     * This method is called just after the [send] method.
     * The state is ensured to be valid before this method is called.
     */
    protected abstract suspend fun onSend(command: Command)

    /**
     * Checks whether the current [state] is not of type [T].
     * If it is, an [IllegalStateException] is thrown with a describing message.
     */
    protected inline fun <reified T : State> requireStateIsNot() {
        if (state !is T) return
        throwStateError()
    }

    /**
     * Checks whether the current [state] is of type [T].
     * If it isn't, an [IllegalStateException] is thrown with a describing message.
     */
    protected inline fun <reified T : State> requireState() {
        if (state is T) return
        throwStateError()
    }

    /**
     * Throws an [IllegalStateException] with a describing message based on the current [state].
     */
    protected fun throwStateError(): Nothing {
        when (state) {
            is State.Stopped -> error("The gateway is already stopped.")
            is State.Running -> error("The gateway is already running, call stop() first.")
            is State.Detached -> error("The Gateway has been detached and can no longer be used, create a new instance instead.")
        }
    }

    /**
     * Represents the current state of the gateway.
     * @param retry whether the gateway should attempt to reconnect when it stops.
     */
    protected sealed class State(public val retry: Boolean) {
        public object Stopped : State(false)
        public class Running(retry: Boolean) : State(retry)
        public object Detached : State(false)
    }
}
