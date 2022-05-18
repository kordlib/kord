package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope

public class ApplicationCommandPermissionsUpdateEvent(
    public val permissions: ApplicationCommandPermissions,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord),
) : Event, CoroutineScope by coroutineScope
