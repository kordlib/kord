package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity
import kotlinx.serialization.descriptors.SerialDescriptor

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addEntityKDoc() {
    val docLink = "See [%T]s in the [Discord·Developer·Documentation]($docUrl)."
    val combinedKDocFormat = if (kDoc != null) "$kDoc\n\n$docLink" else docLink
    addKdoc(combinedKDocFormat, entityCN)
}

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addUnknownClass(constructorParameterName: String, constructorParameterType: TypeName) =
    addClass("Unknown") {
        addKdoc(
            "An unknown [%1T].\n\nThis is used as a fallback for [%1T]s that haven't been added to Kord yet.",
            entityCN,
        )
        addModifiers(PUBLIC)
        primaryConstructor {
            addModifiers(INTERNAL)
            addParameter(constructorParameterName, constructorParameterType)
        }
        superclass(entityCN)
        addSuperclassConstructorParameter(constructorParameterName)
    }

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addEntityEntries() {
    for (entry in this@GenerationEntity.entries) {
        addObject(entry.name) {
            entry.kDoc?.let { addKdoc(it) }
            @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Deprecated`
            entry.deprecated?.let { addAnnotation(it) }
            entry.requiresOptInAnnotations.forEach { annotation ->
                addAnnotation(ClassName.bestGuess(annotation))
            }
            addModifiers(PUBLIC)
            superclass(entityCN)
            addSuperclassConstructorParameter(valueFormat, entry.value)
        }
    }
}

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addSharedSerializerContent(serializedClass: ClassName) {
    addModifiers(INTERNAL)
    addSuperinterface(K_SERIALIZER.parameterizedBy(serializedClass))
    addProperty<SerialDescriptor>("descriptor", OVERRIDE) {
        initializer(
            "%M(%S, %T)",
            PRIMITIVE_SERIAL_DESCRIPTOR,
            serializedClass.canonicalName,
            valueType.toPrimitiveKind(),
        )
    }
}

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addSharedCompanionObjectContent() {
    addModifiers(PUBLIC)
    addProperty("entries", LIST.parameterizedBy(entityCN), PUBLIC) {
        addKdoc("A [List] of all known [%T]s.", entityCN)
        addEntryOptIns()
        delegate {
            withControlFlow("lazy(mode·=·%M)", LazyThreadSafetyMode.PUBLICATION.asMemberName()) {
                addStatement("listOf(")
                withIndent {
                    for (entry in entriesDistinctByValue) {
                        addStatement("${entry.nameWithSuppressedDeprecation},")
                    }
                }
                addStatement(")")
            }
        }
    }
}
