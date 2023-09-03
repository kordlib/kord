package dev.kord.ksp.generation.kordenum

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.KModifier.*
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
        addFunction("toString") {
            addModifiers(FINAL, OVERRIDE)
            returns<String>()
            addStatement("return \"$entityName.\${this::class.simpleName}($valueName=$$valueName)\"")
        }
        addClass("Unknown") {
            addSharedUnknownClassContent()
            primaryConstructor {
                addParameter(valueName, valueCN)
            }
            addSuperclassConstructorParameter(valueName)
        }
        addEntityEntries()
        addSerializer()
        addCompanionObject {
            addSharedCompanionObjectContent()
        }
    }
}
