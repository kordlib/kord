package dev.kord.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("DEPRECATION")
@Deprecated(
    "This is no longer documented. Use 'InviteTargetType' instead.",
    ReplaceWith("InviteTargetType", "dev.kord.common.entity.InviteTargetType"),
)
@Serializable(with = TargetUserType.Serializer::class)
public sealed class TargetUserType(public val value: Int) {
    public class Unknown(value: Int) : TargetUserType(value)
    public object Stream : TargetUserType(1)

    internal object Serializer : KSerializer<TargetUserType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Kord.TargetUserType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TargetUserType = when (val value = decoder.decodeInt()) {
            1 -> Stream
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: TargetUserType) {
            encoder.encodeInt(value.value)
        }
    }

}
