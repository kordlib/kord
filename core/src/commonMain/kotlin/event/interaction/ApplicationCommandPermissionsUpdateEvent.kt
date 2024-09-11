package dev.kord.core.event.interaction

import dev.kord.core.Kord
import dev.kord.core.entity.application.ApplicationCommandPermissions
import dev.kord.core.event.Event

/**
 * The event dispatched when an [ApplicationCommandPermissions] are updated.
 *
 * See [application command permissions update](https://discord.com/developers/docs/interactions/application-commands#edit-application-command-permissions)
 *
 * @param permissions The [ApplicationCommandPermissions] for the command
 */
public class ApplicationCommandPermissionsUpdateEvent(
    public val permissions: ApplicationCommandPermissions,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
) : Event
