package dev.kord.rest.json.request

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@KordPreview
data class ApplicationCommandCreateRequest(
    val name: String,
    val description: String,
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing()
)

@Serializable
@KordPreview
data class ApplicationCommandModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing(),
    val options: Optional<List<ApplicationCommandOption>> = Optional.Missing()
)

@Serializable
@KordPreview
data class InteractionResponseModifyRequest(
    val content: Optional<String> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
)

@KordPreview
data class MultipartInteractionResponseModifyRequest(
    val request: InteractionResponseModifyRequest,
    val files: List<Pair<String, java.io.InputStream>> = emptyList(),
)

@Serializable
@KordPreview
data class InteractionResponseCreateRequest(
    val type: InteractionResponseType,
    val data: Optional<InteractionApplicationCommandCallbackData> = Optional.Missing()
)

@Serializable
@KordPreview
class InteractionApplicationCommandCallbackData(
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val content: Optional<String> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
    val flags: Optional<MessageFlags> = Optional.Missing()

)

@KordPreview
data class MultipartFollowupMessageCreateRequest(
    val request: FollowupMessageCreateRequest,
    val files: List<Pair<String, java.io.InputStream>> = emptyList(),
)

@Serializable
@KordPreview
class FollowupMessageCreateRequest(
    val content: Optional<String> = Optional.Missing(),
    val username: Optional<String> = Optional.Missing(),
    @SerialName("avatar_url")
    val avatar: Optional<String> = Optional.Missing(),
    val tts: OptionalBoolean = OptionalBoolean.Missing,
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing()
)

@Serializable
@KordPreview
data class FollowupMessageModifyRequest(
    val content: Optional<String> = Optional.Missing(),
    val embeds: Optional<List<EmbedRequest>> = Optional.Missing(),
    @SerialName("allowed_mentions")
    val allowedMentions: Optional<AllowedMentions> = Optional.Missing(),
)


@Serializable
@KordPreview
data class PingInteractionRequest(
    val id: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    val type: InteractionType,
    val token: String,
    val user: DiscordUser,
    val version: Int
)

@Serializable
@KordPreview
object PingInteractionResponse {
    val serializer = object : KSerializer<PingInteractionResponse> {

        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("PingInteractionResponse") {
                element("type", Int.serializer().descriptor)
            }

        override fun deserialize(decoder: Decoder): PingInteractionResponse {
            throw UnsupportedOperationException()
        }


        override fun serialize(encoder: Encoder, value: PingInteractionResponse) {
            val composite = encoder.beginStructure(descriptor)
            composite.encodeIntElement(descriptor, 0, InteractionResponseType.Pong.type)
            composite.endStructure(descriptor)
        }

    }
}