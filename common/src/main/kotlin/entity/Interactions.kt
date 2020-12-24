package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class DiscordApplicationCommand(
    val id: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    val name: String,
    val description: String,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing()
)

@Serializable
class ApplicationCommandOption(
    val type: ApplicationCommandOptionType,
    val name: String,
    val description: String,
    val default: OptionalBoolean = OptionalBoolean.Missing,
    val required: OptionalBoolean = OptionalBoolean.Missing,
    val choices: Optional<List<DiscordApplicationCommandOptionChoice>> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing()
)

@Serializable(ApplicationCommandOptionType.Serializer::class)
sealed class ApplicationCommandOptionType(val type: Int) {


    object SubCommand : ApplicationCommandOptionType(1)
    object SubCommandGroup : ApplicationCommandOptionType(2)
    object String : ApplicationCommandOptionType(3)
    object Integer : ApplicationCommandOptionType(4)
    object Boolean : ApplicationCommandOptionType(5)
    object User : ApplicationCommandOptionType(6)
    object Channel : ApplicationCommandOptionType(7)
    object Role : ApplicationCommandOptionType(8)
    object Unknown : ApplicationCommandOptionType(Int.MAX_VALUE)

    companion object

    object Serializer : KSerializer<ApplicationCommandOptionType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("ApplicationCommandOptionType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ApplicationCommandOptionType {
            return when (decoder.decodeInt()) {
                1 -> SubCommand
                2 -> SubCommandGroup
                3 -> String
                4 -> Integer
                5 -> Boolean
                6 -> User
                7 -> Channel
                8 -> Role
                else -> Unknown
            }
        }

        override fun serialize(encoder: Encoder, value: ApplicationCommandOptionType) {
            encoder.encodeInt(value.type)
        }
    }


}

@Serializable
data class DiscordApplicationCommandOptionChoice(
    val name: String,
    val value: String // mixed type int or string
)

@Serializable
data class DiscordInteraction(
    val id: Snowflake,
    val type: InteractionType,
    val data: DiscordApplicationCommandInteractionData,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake,
    val member: DiscordGuildMember,
    val token: String,
    val version: Int
)
@Serializable(InteractionType.Serializer::class)
sealed class InteractionType(val type: Int) {
    object Ping : InteractionType(1)
    object ApplicationCommand : InteractionType(2)
    object Unknown : InteractionType(Int.MAX_VALUE)

    companion object

    object Serializer : KSerializer<InteractionType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("InteractionType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): InteractionType {
            return when (decoder.decodeInt()) {
                1 -> Ping
                2 -> ApplicationCommand
                else -> Unknown
            }
        }

        override fun serialize(encoder: Encoder, value: InteractionType) {
            encoder.encodeInt(value.type)
        }

    }
}
@Serializable
data class DiscordApplicationCommandInteractionData(
    val id: Snowflake,
    val name: String,
    val options: Optional<List<DiscordApplicationCommandInteractionDataOption>> = Optional.Missing()
)

@Serializable
data class DiscordApplicationCommandInteractionDataOption(
    val name: String,
    val value: Optional<String> = Optional.Missing(),
    val options: Optional<List<DiscordApplicationCommandInteractionDataOption>> = Optional.Missing()
)

@Serializable
data class DiscordInteractionResponse(
    val type: InteractionResponseType,
    val data: Optional<DiscordInteractionApplicationCommandCallbackData> = Optional.Missing()
)

@Serializable(InteractionResponseType.Serializer::class)
sealed class InteractionResponseType(val type: Int) {
    object Pong : InteractionResponseType(1)
    object Acknowledge : InteractionResponseType(2)
    object ChannelMessage : InteractionResponseType(3)
    object ChannelMessageWithSource : InteractionResponseType(4)
    object ACKWithSource : InteractionResponseType(5)
    object Unknown : InteractionResponseType(Int.MAX_VALUE)

    companion object;

    object Serializer : KSerializer<InteractionResponseType> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("InteractionResponseType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): InteractionResponseType {
            return when (decoder.decodeInt()) {
                1 -> Pong
                2 -> Acknowledge
                3 -> ChannelMessage
                4 -> ChannelMessageWithSource
                5 -> ACKWithSource
                else -> Unknown
            }
        }

        override fun serialize(encoder: Encoder, value: InteractionResponseType) {
            encoder.encodeInt(value.type)
        }

    }
}

@Serializable
class DiscordInteractionApplicationCommandCallbackData(
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val content: String,
    val embeds: Optional<List<DiscordEmbed>> = Optional.Missing(),
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing()

)