@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity
import kotlinx.serialization.descriptors.SerialDescriptor

private val GenerationEntity.entityNamePluralSuffix get() = if (entityName.endsWith('s')) "es" else "s"

context(entity: GenerationEntity, context: GenerationContext)
internal fun TypeSpec.Builder.addEntityKDoc() {
    val docLink = "See [%T]${entity.entityNamePluralSuffix} in the [Discord·Developer·Documentation](${entity.docUrl})."
    val combinedKDocFormat = if (entity.kDoc != null) "${entity.kDoc}\n\n$docLink" else docLink
    addKdoc(combinedKDocFormat, context.entityCN)
}

context(entity: GenerationEntity, context: GenerationContext)
internal fun TypeSpec.Builder.addUnknownClass(constructorParameterName: String, constructorParameterType: TypeName) =
    addClass("Unknown") {
        addKdoc(
            "An unknown [%1T].\n\nThis is used as a fallback for [%1T]${entity.entityNamePluralSuffix} that haven't been " +
                "added to Kord yet.",
            context.entityCN,
        )
        addModifiers(PUBLIC)
        primaryConstructor {
            addModifiers(INTERNAL)
            addParameter(constructorParameterName, constructorParameterType)
        }
        superclass(context.entityCN)
        addSuperclassConstructorParameter(constructorParameterName)
    }

context(entity: GenerationEntity, context: GenerationContext)
internal fun TypeSpec.Builder.addEntityEntries() {
    for (entry in entity.entries) {
        addObject(entry.name) {
            entry.kDoc?.let { addKdoc(it) }
            @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Deprecated`
            entry.deprecated?.let { addAnnotation(it) }
            entry.requiresOptInAnnotations.forEach { annotation ->
                addAnnotation(ClassName.bestGuess(annotation))
            }
            addModifiers(PUBLIC)
            superclass(context.entityCN)
            addSuperclassConstructorParameter(context.valueFormat, entry.value)
        }
    }
}

context(entity: GenerationEntity, _: GenerationContext)
internal fun TypeSpec.Builder.addSharedSerializerContent(serializedClass: ClassName) {
    addModifiers(INTERNAL)
    addSuperinterface(K_SERIALIZER.parameterizedBy(serializedClass))
    addProperty<SerialDescriptor>("descriptor", OVERRIDE) {
        initializer(
            "%M(%S, %T)",
            PRIMITIVE_SERIAL_DESCRIPTOR,
            serializedClass.canonicalName,
            entity.valueType.toPrimitiveKind(),
        )
    }
}

context(entity: GenerationEntity, context: GenerationContext)
internal fun TypeSpec.Builder.addSharedCompanionObjectContent() {
    addModifiers(PUBLIC)
    addProperty("entries", LIST.parameterizedBy(context.entityCN), PUBLIC) {
        addKdoc("A [List] of all known [%T]${entity.entityNamePluralSuffix}.", context.entityCN)
        addEntryOptIns()
        delegate {
            withControlFlow("lazy(mode·=·%M)", LazyThreadSafetyMode.PUBLICATION.asMemberName()) {
                addStatement("listOf(")
                withIndent {
                    for (entry in context.entriesDistinctByValue) {
                        addStatement("${entry.nameWithSuppressedDeprecation},")
                    }
                }
                addStatement(")")
            }
        }
    }
}
