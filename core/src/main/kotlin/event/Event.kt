package dev.kord.core.event

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.entity.Strategizable
import dev.kord.gateway.Gateway
import kotlinx.serialization.json.JsonElement

/**
 * Representation of an event received from the Discord gateway.
 *
 * @see UnknownEvent
 */
public interface Event : KordObject {
    /**
     * The Gateway that spawned this event.
     */
    public val gateway: Gateway get() = kord.gateway.gateways.getValue(shard)

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

/**
 * Representation of an event that has not been documented / added to Kord yet.
 *
 * @property data the raw [json body][JsonElement] of this event if provided
 * @property name the name of the event if provided
 */
public class UnknownEvent(
    public val name: String?,
    public val data: JsonElement?,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override fun toString(): String =
        "UnknownEvent(name=$name, data=$data, kord=$kord, shard=$shard, customContext=$customContext)"
}
