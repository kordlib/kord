package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class DiscordConnection(
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
        val visiblity: DiscordConnectionVisibility,
)

@Serializable(with = DiscordConnectionVisibility.Serializer::class)
sealed class DiscordConnectionVisibility(val value: Int) {
    class Unknown(value: Int) : DiscordConnectionVisibility(value)
    object None : DiscordConnectionVisibility(0)
    object Everyone : DiscordConnectionVisibility(1)

    internal object Serializer : KSerializer<DiscordConnectionVisibility> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.DiscordConnectionVisibility", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): DiscordConnectionVisibility = when(val value = decoder.decodeInt()) {
            0 -> None
            1 -> Everyone
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: DiscordConnectionVisibility) {
            encoder.encodeInt(value.value)
        }
    }

}
