package dev.kord.ksp.generation.kordenum

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.KModifier.INTERNAL
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.KordEnum
import dev.kord.ksp.generation.GenerationEntity.KordEnum.ValueType
import dev.kord.ksp.generation.GenerationEntity.KordEnum.ValueType.INT
import dev.kord.ksp.generation.GenerationEntity.KordEnum.ValueType.STRING
import dev.kord.ksp.generation.shared.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private fun ValueType.toEncodingPostfix() = when (this) {
    INT -> "Int"
    STRING -> "String"
}

internal fun KordEnum.generateFileSpec(originatingFile: KSFile) = fileSpecForGenerationEntity(originatingFile) {
    addClass(entityCN) {
        // for ksp incremental processing
        addOriginatingKSFile(originatingFile)
        addEntityKDoc()
        addAnnotation<Serializable> {
            addMember("with·=·%T.Serializer::class", entityCN)
        }
        addAnnotation(OPT_IN) {
            addMember("%T::class", KORD_UNSAFE)
        }
        addEntityPrimaryConstructor()
        addEntityEqualsHashCodeToString()
        addClass("Unknown") {
            addSharedUnknownClassContent()
        }
        addEntityEntries()
        addObject("Serializer") {
            addModifiers(INTERNAL)
            addSuperinterface(K_SERIALIZER.parameterizedBy(entityCN))
            addProperty<SerialDescriptor>("descriptor", OVERRIDE) {
                initializer(
                    "%M(%S, %T)",
                    PRIMITIVE_SERIAL_DESCRIPTOR,
                    entityCN.canonicalName,
                    valueType.toPrimitiveKind(),
                )
            }
            val encodingPostfix = valueType.toEncodingPostfix()
            addFunction("serialize") {
                addModifiers(OVERRIDE)
                addParameter<Encoder>("encoder")
                addParameter("value", entityCN)
                addStatement("return encoder.encode$encodingPostfix(value.$valueName)")
            }
            addFunction("deserialize") {
                addModifiers(OVERRIDE)
                addParameter<Decoder>("decoder")
                withControlFlow("return when·(val·$valueName·=·decoder.decode$encodingPostfix())") {
                    for (entry in entriesDistinctByValue) {
                        addStatement("$valueFormat·->·${entry.nameWithSuppressedDeprecation}", entry.value)
                    }
                    addStatement("else·->·Unknown($valueName)")
                }
            }
        }
        addCompanionObject {
            addSharedCompanionObjectContent()
        }
    }
}
