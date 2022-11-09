@file:GenerateKordEnum(
    name = "DiscordConnectionVisibility", valueType = INT,
    docUrl = "https://discord.com/developers/docs/resources/user#connection-object-visibility-types",
    entries = [
        Entry("None", intValue = 0, kDoc = "Invisible to everyone except the user themselves."),
        Entry("Everyone", intValue = 1, kDoc = "Visible to everyone."),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A representation of the D[Discord Connection Object structure](https://discord.com/developers/docs/resources/user#connection-object).
 * The connection object that the user has attached.
 *
 * @param id The id of the connection account.
 * @param name the username of the connection account.
 * @param type The service of the connection (twitch, youtube).
 * @param revoked Whether the connection is revoked.
 * @param integrations A list of partial server integrations.
 * @param verified Whether the connection is verified.
 * @param friendSync Whether friend sync is enabled for this connection.
 * @param showActivity Whether activities related to this connection will be shown in presence updates.
 * @param visibility The visibility of this connection.
 */
@Serializable
public data class DiscordConnection(
    val id: String,
    val name: String,
    val type: String,
    val revoked: OptionalBoolean = OptionalBoolean.Missing,
    val integrations: Optional<List<DiscordIntegration>> = Optional.Missing(),
    val verified: Boolean,
    @SerialName("friend_sync")
    val friendSync: Boolean,
    @SerialName("show_activity")
    val showActivity: Boolean,
    val visibility: DiscordConnectionVisibility,
)
