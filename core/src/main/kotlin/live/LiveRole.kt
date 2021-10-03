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
import kotlinx.coroutines.*

@KordPreview
public fun Role.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveRole = LiveRole(this, coroutineScope)

@KordPreview
public inline fun Role.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveRole.() -> Unit
): LiveRole = this.live(coroutineScope).apply(block)

@Deprecated(
    "The block is not called when the entity is deleted because the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public fun LiveRole.onDelete(scope: CoroutineScope = this, block: suspend (RoleDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public fun LiveRole.onUpdate(scope: CoroutineScope = this, block: suspend (RoleUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@Deprecated(
    "The block is not called when the live entity is shut down",
    ReplaceWith("coroutineContext.job.invokeOnCompletion(block)", "kotlinx.coroutines.job"),
    DeprecationLevel.ERROR
)
@KordPreview
public inline fun LiveRole.onShutdown(scope: CoroutineScope = this, crossinline block: suspend (Event) -> Unit): Job =
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
public fun LiveRole.onGuildDelete(scope: CoroutineScope = this, block: suspend (GuildDeleteEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

@KordPreview
public class LiveRole(
    role: Role,
    coroutineScope: CoroutineScope = role.kord + SupervisorJob(role.kord.coroutineContext.job)
) : AbstractLiveKordEntity(role.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = role.id

    public var role: Role = role
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is RoleDeleteEvent -> role.id == event.roleId
        is RoleUpdateEvent -> role.id == event.role.id
        is GuildDeleteEvent -> role.guildId == event.guildId
        else -> false
    }

    override fun update(event: Event): Unit = when (event) {
        is RoleDeleteEvent -> shutDown(LiveCancellationException(event, "The role is deleted"))
        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))
        is RoleUpdateEvent -> role = event.role
        else -> Unit
    }

}
