package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.event.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job

public class ApplicationCommandPermissionsUpdateEvent(
    public val permissions: ApplicationCommandPermissions,
    override val kord: Kord,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(kord),
) : Event, CoroutineScope by coroutineScope
