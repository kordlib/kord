package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.event.Event

public class ApplicationCommandPermissionsUpdateEvent(
    public val permissions: ApplicationCommandPermissions,
    override val kord: Kord,
    override val shard: Int,
) : Event
