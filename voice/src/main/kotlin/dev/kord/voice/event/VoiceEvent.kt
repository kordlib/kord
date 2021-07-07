package dev.kord.voice.event

import dev.kord.voice.VoiceOpCode
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

sealed class VoiceEvent {
    abstract val op: VoiceOpCode

    companion object FrameSerializer : JsonContentPolymorphicSerializer<VoiceEvent>(VoiceEvent::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out VoiceEvent> =
            when (VoiceOpCode.of(element.jsonObject["op"]!!.jsonPrimitive.int)) {
//                VoiceOpCode.Identify -> IdentifyFrame.serializer()
//                VoiceOpCode.SelectProtocol -> SelectProtocolFrame.serializer()
                VoiceOpCode.Ready -> ReadyVoiceEvent.serializer()
//                VoiceOpCode.Heartbeat -> HeartbeatFrame.serializer()
                VoiceOpCode.SessionDescription -> SessionDescriptionEvent.serializer()
                VoiceOpCode.Speaking -> VoiceSpeakingEvent.serializer()
                VoiceOpCode.HeartbeatACK -> VoiceHeartbeatACKEvent.serializer()
//                VoiceOpCode.Resume -> ResumeFrame.serializer()
                VoiceOpCode.Hello -> HelloVoiceEvent.serializer()
                VoiceOpCode.Resumed -> VoiceResumedEvent.serializer()
//                VoiceOpCode.ClientDisconnect -> ClientDisconnected.serializer()
//                VoiceOpCode.CODEC_HINT -> CodecHintFrame.serializer()
                VoiceOpCode.Unknown -> throw NotImplementedError("Discord sent us a unknown Audio Opcode of ${element.jsonObject["op"]!!.jsonPrimitive.int}!")
                else -> error("lol what")
            }
    }
//    companion object : DeserializationStrategy<VoiceEvent?> {
//        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("VoiceEvent") {
//            element("op", VoiceOpCode.descriptor)
//            element("d", JsonObject.serializer().descriptor)
//        }
//
//        override fun deserialize(decoder: Decoder): VoiceEvent? {
//
//            lateinit var voiceOpCode: VoiceOpCode
//            var deserializedEvent: VoiceEvent? = null
//
//            with(decoder.beginStructure(descriptor)) {
//                loop@ while (true) {
//                    when (val index = decodeElementIndex(descriptor)) {// we assume the all fields to be present *before* the data field
//                        CompositeDecoder.DECODE_DONE -> break@loop
//                        0 -> {
//                            voiceOpCode = VoiceOpCode.deserialize(decoder)
//                        }
//                        1 -> {
//                            @Suppress("NON_EXHAUSTIVE_WHEN")
//                            when (voiceOpCode) {
//                                VoiceOpCode.HeartbeatACK -> deserializedEvent =
//                                    decodeSerializableElement(descriptor, index, VoiceHeartbeatACKEvent.serializer())
//                                VoiceOpCode.Resumed -> deserializedEvent = VoiceResumedEvent
//                                VoiceOpCode.Ready -> deserializedEvent =
//                                    decodeSerializableElement(descriptor, index, ReadyVoiceEvent.serializer())
//                                VoiceOpCode.Hello -> deserializedEvent =
//                                    decodeSerializableElement(descriptor, index, HelloVoiceEvent.serializer())
//
//                                VoiceOpCode.Speaking -> deserializedEvent =
//                                    decodeSerializableElement(descriptor, index, VoiceSpeakingEvent.serializer())
//                                VoiceOpCode.SessionDescription -> decodeSerializableElement(
//                                    descriptor,
//                                    index,
//                                    SessionDescription.serializer()
//                                )
//                            }
//                        }
//                    }
//                }
//
//                endStructure(descriptor)
//            }
//            return deserializedEvent
//        }
//
//
//    }
}
