package dev.kord.ksp.generation.kordenum

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.KModifier.PUBLIC
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.codegen.kotlinpoet.addAnnotation
import dev.kord.codegen.kotlinpoet.addClass
import dev.kord.codegen.kotlinpoet.addCompanionObject
import dev.kord.codegen.kotlinpoet.addFunction
import dev.kord.codegen.kotlinpoet.addProperty
import dev.kord.codegen.kotlinpoet.primaryConstructor
import dev.kord.codegen.kotlinpoet.withControlFlow
import dev.kord.ksp.generation.GenerationEntity.KordEnum
import dev.kord.ksp.generation.shared.*
import kotlinx.serialization.Serializable

internal fun KordEnum.generateFileSpec(originatingFile: KSFile) = fileSpecForGenerationEntity(originatingFile) {
    addClass(entityCN) {
        // for ksp incremental processing
        addOriginatingKSFile(originatingFile)
        addEntityKDoc()
        addAnnotation<Serializable> {
            addMember("with·=·%T.Serializer::class", entityCN)
        }
        addModifiers(PUBLIC, SEALED)
        primaryConstructor {
            addParameter(valueName, valueCN)
        }
        addProperty(valueName, valueCN, PUBLIC) {
            addKdoc("The raw $valueName used by Discord.")
            initializer(valueName)
        }
        addEqualsAndHashCodeBasedOnClassAndSingleProperty(entityCN, property = valueName, isFinal = true)
        addEntityToString(property = valueName)
        addUnknownClass(constructorParameterName = valueName, constructorParameterType = valueCN)
        addEntityEntries()
        addSerializer()
        addCompanionObject {
            addSharedCompanionObjectContent()
            addFunction("from") {
                addKdoc(
                    "Returns an instance of [%1T] with [%1T.$valueName] equal to the specified [$valueName].",
                    entityCN,
                )
                addParameter(valueName, valueCN)
                returns(entityCN)
                withControlFlow("return when·($valueName)") {
                    for (entry in entriesDistinctByValue) {
                        addStatement("$valueFormat·->·${entry.nameWithSuppressedDeprecation}", entry.value)
                    }
                    addStatement("else·->·Unknown($valueName)")
                }
            }
        }
    }
}
