package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Role
import dev.kord.core.event.Event
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.role.RoleDeleteEvent
import dev.kord.core.event.role.RoleUpdateEvent
import dev.kord.core.live.exception.LiveCancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@KordPreview
fun Role.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord
) = LiveRole(this, dispatcher, parent)

@KordPreview
inline fun Role.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = kord,
    block: LiveRole.() -> Unit
) = this.live(dispatcher, parent).apply(block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveRole.onDelete(scope: CoroutineScope = this, block: suspend (RoleDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
fun LiveRole.onUpdate(scope: CoroutineScope = this, block: suspend (RoleUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
inline fun LiveRole.onShutdown(scope: CoroutineScope = this, crossinline block: suspend (Event) -> Unit) =
    on<Event>(scope) {
        if (it is RoleDeleteEvent || it is GuildDeleteEvent) {
            block(it)
        }
    }

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
fun LiveRole.onGuildDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
class LiveRole(
    role: Role,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: CoroutineScope = role.kord
) : AbstractLiveKordEntity(role.kord, dispatcher, parent), KordEntity {

    override val id: Snowflake
        get() = role.id

    var role = role
        private set

    override fun filter(event: Event) = when (event) {
        is RoleDeleteEvent -> role.id == event.roleId
        is RoleUpdateEvent -> role.id == event.role.id
        is GuildDeleteEvent -> role.guildId == event.guildId
        else -> false
    }

    override fun update(event: Event) = when (event) {
        is RoleDeleteEvent -> shutDown(LiveCancellationException(event, "The role is deleted"))
        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))
        is RoleUpdateEvent -> role = event.role
        else -> Unit
    }

}
