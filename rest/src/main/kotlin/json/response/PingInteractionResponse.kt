package dev.kord.rest.json.response

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.InteractionResponseType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with=PingInteractionResponse.Serializer::class)
@KordPreview
object PingInteractionResponse {
    object Serializer : KSerializer<PingInteractionResponse> {

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