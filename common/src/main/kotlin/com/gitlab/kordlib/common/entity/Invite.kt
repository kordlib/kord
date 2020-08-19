package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TargetUserType.TargetUserTypeSerializer::class)
enum class TargetUserType(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    STREAM(1);

    @Serializer(forClass = TargetUserType::class)
    companion object TargetUserTypeSerializer : KSerializer<TargetUserType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TargetUserType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TargetUserType {
            val code = decoder.decodeInt()

            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: TargetUserType) {
            encoder.encodeInt(value.code)
        }
    }

}