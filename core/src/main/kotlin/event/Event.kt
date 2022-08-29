package dev.kord.core.event

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.entity.Strategizable
import dev.kord.gateway.Gateway

public interface Event {
    /**
     * The Gateway that spawned this event.
     */
    public val gateway: Gateway get() = kord.gateway.gateways.getValue(shard)

    public val kord: Kord

    /**
     * The shard number of the [gateway] that spawned this event.
     */
    public val shard: Int

    /**
     * A custom object that can be inserted when creating events. By default, this is just `null`.
     *
     * This can be used to associate a custom context with an event, e.g. like this:
     * ```kotlin
     * class YourCustomContext(...)
     *
     * val kord = Kord(token) {
     *     gatewayEventInterceptor = DefaultGatewayEventInterceptor(
     *         customContextCreator = { event, kord -> YourCustomContext(...) }
     *     )
     * }
     *
     * kord.on<MessageCreateEvent> {
     *     // receive the value previously set when creating the event
     *     val context = customContext as YourCustomContext
     *     // ...
     * }
     *
     * kord.login()
     * ```
     *
     * Note that [withStrategy][Strategizable.withStrategy] for [strategizable][Strategizable] [Event]s will copy
     * [customContext] only by reference. This should be considered when inserting mutable objects into [customContext].
     */
    @KordPreview
    public val customContext: Any?
}
