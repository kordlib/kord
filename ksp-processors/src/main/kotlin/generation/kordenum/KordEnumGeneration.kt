package dev.kord.ksp.generation.kordenum

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.KModifier.PUBLIC
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.KordEnum
import dev.kord.ksp.generation.shared.*
import kotlinx.serialization.Serializable

internal fun KordEnum.generateFileSpec(originatingFile: KSFile) = fileSpecForGenerationEntity(originatingFile) {
    addClass(currentContext.entityCN) {
        // for ksp incremental processing
        addOriginatingKSFile(originatingFile)
        addEntityKDoc()
        addAnnotation<Serializable> {
            addMember("with·=·%T.Serializer::class", currentContext.entityCN)
        }
        addModifiers(PUBLIC, SEALED)
        primaryConstructor {
            addParameter(valueName, currentContext.valueCN)
        }
        addProperty(valueName, currentContext.valueCN, PUBLIC) {
            addKdoc("The raw $valueName used by Discord.")
            initializer(valueName)
        }
        addEqualsAndHashCodeBasedOnClassAndSingleProperty(currentContext.entityCN, property = valueName, isFinal = true)
        addEntityToString(property = valueName)
        addUnknownClass(constructorParameterName = valueName, constructorParameterType = currentContext.valueCN)
        addEntityEntries()
        addSerializer()
        addCompanionObject {
            addSharedCompanionObjectContent()
            addFunction("from") {
                addKdoc(
                    "Returns an instance of [%1T] with [%1T.$valueName] equal to the specified [$valueName].",
                    currentContext.entityCN,
                )
                addParameter(valueName, currentContext.valueCN)
                returns(currentContext.entityCN)
                withControlFlow("return when·($valueName)") {
                    for (entry in currentContext.entriesDistinctByValue) {
                        addStatement("${currentContext.valueFormat}·->·${entry.nameWithSuppressedDeprecation}", entry.value)
                    }
                    addStatement("else·->·Unknown($valueName)")
                }
            }
        }
    }
}
