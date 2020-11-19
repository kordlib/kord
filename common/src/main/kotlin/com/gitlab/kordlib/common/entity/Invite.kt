package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TargetUserType.Serializer::class)
sealed class TargetUserType(val value: Int) {
    class Unknown(value: Int) : TargetUserType(value)
    object Stream : TargetUserType(1)

    internal object Serializer : KSerializer<TargetUserType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Kord.TargetUserType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TargetUserType = when(val value = decoder.decodeInt()) {
            1 -> Stream
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: TargetUserType) {
            encoder.encodeInt(value.value)
        }
    }

}