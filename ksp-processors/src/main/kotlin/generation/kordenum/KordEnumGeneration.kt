package dev.kord.ksp.generation.kordenum

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.NOTHING
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import dev.kord.ksp.*
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
        addClass("Unknown") {
            addSharedUnknownClassContent()
            primaryConstructor {
                addModifiers(INTERNAL)
                addParameter(valueName, valueCN)
                if (unknownConstructorWasPublic) addParameter("unused", type = NOTHING.copy(nullable = true)) {
                    @OptIn(DelicateKotlinPoetApi::class)
                    addAnnotation(Suppress("UNUSED_PARAMETER"))
                }
            }
            addSuperclassConstructorParameter(valueName)
            // TODO bump deprecation level and remove eventually (also share code with bit flags then)
            if (unknownConstructorWasPublic) addConstructor {
                @OptIn(DelicateKotlinPoetApi::class)
                addAnnotation(
                    Deprecated(
                        "Replaced by '$entityName.from()'.",
                        ReplaceWith("$entityName.from($valueName)", entityCN.canonicalName),
                        DeprecationLevel.HIDDEN,
                    )
                )
                addModifiers(PUBLIC)
                addParameter(valueName, valueCN)
                callThisConstructor(valueName, "null")
            }
        }
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
                    addStatement(if (unknownConstructorWasPublic) "else·->·Unknown($valueName,·null)" else "else·->·Unknown($valueName)")
                }
            }
        }
    }
}
