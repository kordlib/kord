package dev.kord.ksp.generation.generator.enum

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.kord.ksp.*
import dev.kord.ksp.generation.*
import dev.kord.ksp.generation.GenerationEntity.KordEnum
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

context(KordEnum, ProcessingContext, FileSpec.Builder)
fun TypeSpec.Builder.addKordEnumSerializer() {
    addAnnotation<Serializable> {
        addMember("with·=·%T.Serializer::class", entityCN)
    }
    addAnnotation(OPT_IN) {
        addMember("%T::class", KORD_UNSAFE)
    }

    addObject("Serializer") {
        addModifiers(KModifier.INTERNAL)
        addSuperinterface(K_SERIALIZER.parameterizedBy(entityCN))

        addProperty<SerialDescriptor>("descriptor", KModifier.OVERRIDE) {
            initializer(
                "%M(%S, %T)",
                PRIMITIVE_SERIAL_DESCRIPTOR,
                entityCN.canonicalName,
                valueType.toPrimitiveKind(),
            )
        }

        val encodingPostfix = valueType.toEncodingPostfix()

        addFunction("serialize") {
            addModifiers(KModifier.OVERRIDE)
            addParameter<Encoder>("encoder")
            addParameter("value", entityCN)
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
