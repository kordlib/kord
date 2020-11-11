package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.UserFlags
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class MessageCreateRequest(
        val content: String? = null,
        val nonce: String? = null,
        val tts: Boolean? = null,
        val embed: EmbedRequest? = null,
        @SerialName("allowed_mentions")
        val allowedMentions: AllowedMentions? = null,
)

@Serializable
data class AllowedMentions(
        val parse: List<AllowedMentionType>,
        val users: List<String>,
        val roles: List<String>,
)

@Serializable(with = AllowedMentionType.Serializer::class)
sealed class AllowedMentionType(val value: String) {
    class Unknown(value: String) : AllowedMentionType(value)
    object RoleMentions : AllowedMentionType("roles")
    object UserMentions : AllowedMentionType("users")
    object EveryoneMentions : AllowedMentionType("everyone")

    internal class Serializer : KSerializer<AllowedMentionType> {
            override val descriptor: SerialDescriptor
                    get() = PrimitiveSerialDescriptor("Kord.DiscordAllowedMentionType", PrimitiveKind.STRING)

            override fun deserialize(decoder: Decoder): AllowedMentionType = when(val value = decoder.decodeString()) {
                    "roles" -> RoleMentions
                    "users" -> UserMentions
                    "everyone" -> EveryoneMentions
                    else -> Unknown(value)
            }

            override fun serialize(encoder: Encoder, value: AllowedMentionType) {
                    encoder.encodeString(value.value)
            }
    }
}

data class MultipartMessageCreateRequest(
        val request: MessageCreateRequest,
        val files: List<Pair<String, java.io.InputStream>> = emptyList(),
)

@Serializable
data class EmbedRequest(
        val title: String?,
        val type: String?,
        val description: String?,
        val url: String?,
        val timestamp: String? = null,
        val color: Int? = null,
        val footer: EmbedFooterRequest? = null,
        val image: EmbedImageRequest? = null,
        val thumbnail: EmbedThumbnailRequest? = null,
        val author: EmbedAuthorRequest? = null,
        val fields: List<EmbedFieldRequest>? = null,
)


@Serializable
data class EmbedFooterRequest(
        val text: String,
        @SerialName("icon_url")
        val iconUrl: String? = null,
)

@Serializable
data class EmbedImageRequest(val url: String)

@Serializable
data class EmbedThumbnailRequest(val url: String)

@Serializable
data class EmbedAuthorRequest(
        val name: String? = null,
        val url: String? = null,
        @SerialName("icon_url")
        val iconUrl: String? = null,
)

@Serializable
data class EmbedFieldRequest(
        val name: String,
        val value: String,
        val inline: Boolean? = null,
)

@Serializable
data class MessageEditPatchRequest(
        val content: String? = null,
        val embed: EmbedRequest? = null,
        val flags: UserFlags? = null,
        @SerialName("allowed_mentions")
        val allowedMentions: AllowedMentions? = null,
)

@Serializable
data class BulkDeleteRequest(val messages: List<Snowflake>)
