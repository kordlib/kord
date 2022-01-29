package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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

@Serializable(with = DiscordConnectionVisibility.Serializer::class)
public sealed class DiscordConnectionVisibility(public val value: Int) {
    public class Unknown(value: Int) : DiscordConnectionVisibility(value)

    /**
     * The connection is invisible to everyone except the user themselves.
     */
    public object None : DiscordConnectionVisibility(0)

    /**
     * The connection is visible to everyone.
     */
    public object Everyone : DiscordConnectionVisibility(1)

    internal object Serializer : KSerializer<DiscordConnectionVisibility> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.DiscordConnectionVisibility", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): DiscordConnectionVisibility =
            when (val value = decoder.decodeInt()) {
                0 -> None
                1 -> Everyone
                else -> Unknown(value)
            }

        override fun serialize(encoder: Encoder, value: DiscordConnectionVisibility) {
            encoder.encodeInt(value.value)
        }
    }

}
