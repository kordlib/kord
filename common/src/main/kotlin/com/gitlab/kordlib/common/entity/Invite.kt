package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*

@Serializable(with = TargetUserType.TargetUserTypeSerializer::class)
enum class TargetUserType(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    STREAM(1);

    @Serializer(forClass = TargetUserType::class)
    companion object TargetUserTypeSerializer : KSerializer<TargetUserType> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptor("TargetUserType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): TargetUserType {
            val code = decoder.decodeInt()

            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: TargetUserType) {
            encoder.encodeInt(obj.code)
        }
    }

}