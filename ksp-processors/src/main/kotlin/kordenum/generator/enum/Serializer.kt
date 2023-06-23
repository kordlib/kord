package dev.kord.ksp.kordenum.generator.enum

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.kord.ksp.*
import dev.kord.ksp.kordenum.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

context(KordEnum, ProcessingContext, FileSpec.Builder)
fun TypeSpec.Builder.addEnumSerializer() {
    addAnnotation<Serializable> {
        addMember("with·=·%T.Serializer::class", enumName)
    }
    addAnnotation(OPT_IN) {
        addMember("%T::class", KORD_UNSAFE)
    }

    addObject("Serializer") {
        addModifiers(KModifier.INTERNAL)
        addSuperinterface(K_SERIALIZER.parameterizedBy(enumName))

        addProperty<SerialDescriptor>("descriptor", KModifier.OVERRIDE) {
            initializer(
                "%M(%S, %T)",
                PRIMITIVE_SERIAL_DESCRIPTOR,
                enumName.canonicalName,
                valueType.toPrimitiveKind(),
            )
        }

        addFunction("serialize") {
            addModifiers(KModifier.OVERRIDE)
            addParameter<Encoder>("encoder")
            addParameter("value", enumName)
            addStatement("return encoder.encode$encodingPostfix(value.$valueName)")
        }

        addFunction("deserialize") {
            addModifiers(KModifier.OVERRIDE)
            addParameter<Decoder>("decoder")
            withControlFlow("return when·(val·$valueName·=·decoder.decode$encodingPostfix())") {
                for (entry in relevantEntriesForSerializerAndCompanion) {
                    addStatement("$valueFormat·->·${entry.warningSuppressedName}", entry.value)
                }
                addStatement("else·->·Unknown($valueName)")
            }
        }
    }
}
