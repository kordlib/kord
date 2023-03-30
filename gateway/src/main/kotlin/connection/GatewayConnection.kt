package dev.kord.gateway.connection

import dev.kord.common.entity.DiscordShard
import dev.kord.common.ratelimit.RateLimiter
import dev.kord.gateway.Command
import dev.kord.gateway.Event
import dev.kord.gateway.Identify
import dev.kord.gateway.Resume
import dev.kord.gateway.ratelimit.IdentifyRateLimiter
import dev.kord.gateway.retry.Retry
import io.ktor.client.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import java.net.URI
import kotlin.time.Duration

/**
 * Represents a connection to the Discord gateway.
 * Lifecycle of a connection is very simple: [open] and [close].
 */
public interface GatewayConnection {

    public val ping: StateFlow<Duration?>

    /**
     * Opens the connection to the Discord gateway.
     * @param data the data required to open the connection.
     * @throws IllegalStateException if the connection is already closed/open.
     * @see GatewayConnection.Data
     */
    public suspend fun open(data: Data): CloseReason

    /**
     * Closes the connection to the Discord gateway.
     * @throws IllegalStateException if the connection is already closed, or is not open yet.
     */
    public suspend fun close()

    /**
     * Sends the [command] to the Discord gateway.
     * @throws IllegalStateException if the connection is closed.
     */
    public suspend fun send(command: Command)

    /**
     * Data required to open a connection.
     * @see GatewayConnection.open
     */
    public data class Data(
        val shard: DiscordShard,
        val uri: URI,
        val session: Session,
        val client: HttpClient,
        val json: Json,
        val eventFlow: MutableSharedFlow<Event>,
        val sendRateLimiter: RateLimiter,
        val identifyRateLimiter: IdentifyRateLimiter,
        val reconnectRetry: Retry
    )

    public sealed interface Session {

        public class New(public val identify: Identify) : Session
        public class Resumed(public val resume: Resume) : Session
    }

    /**
     * Represents the reason why a connection was closed.
     */
    public sealed interface CloseReason {

        /**
         * The connection was closed manually.
         */
        public object Manual : CloseReason

        /**
         * The connection was closed due to an invalid session.
         */
        public object InvalidSession : CloseReason

        /**
         * The connection was closed due to an invalid session.
         * Resumable variant.
         */
        public class ResumableInvalidSession(public val resume: Resume) : CloseReason

        /**
         * The connection was closed due to a reconnect required.
         */
        public object Reconnect : CloseReason

        /**
         * The connection was closed due to a reconnect required.
         * Resumable variant.
         */
        public class ResumableReconnect(public val resume: Resume) : CloseReason

        /**
         * The connection was closed due to an error.
         */
        public class Error(public val cause: Throwable) : CloseReason

        /**
         * The connection was closed due to a close frame.
         */
        public class Plain(
            public val code: Int,
            public val message: String?,
            public val resume: Resume?
        ) : CloseReason
    }
}
