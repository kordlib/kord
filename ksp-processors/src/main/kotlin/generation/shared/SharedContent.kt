package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addEntityKDoc() {
    val docLink = "See [%T]s in the [Discord·Developer·Documentation]($docUrl)."
    val combinedKDocFormat = if (kDoc != null) "$kDoc\n\n$docLink" else docLink
    addKdoc(combinedKDocFormat, entityCN)
}

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addEntityEqualsHashCodeToString() {
    addEqualsAndHashCode(entityCN, FINAL)
    addFunction("toString") {
        addModifiers(FINAL, OVERRIDE)
        returns<String>()
        addStatement("return \"$entityName.\${this::class.simpleName}($valueName=\$$valueName)\"")
    }
}

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addSharedUnknownClassContent() {
    addKdoc(
        "An unknown [%1T].\n\nThis is used as a fallback for [%1T]s that haven't been added to Kord yet.",
        entityCN,
    )
    addModifiers(PUBLIC)
    superclass(entityCN)
}

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addEntityEntries() {
    for (entry in entries) {
        addObject(entry.name) {
            entry.kDoc?.let { addKdoc(it) }
            @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Deprecated`
            entry.deprecated?.let { addAnnotation(it) }
            entry.additionalOptInMarkerAnnotations.forEach { annotation ->
                addAnnotation(ClassName.bestGuess(annotation))
            }
            addModifiers(PUBLIC)
            superclass(entityCN)
            addSuperclassConstructorParameter(valueFormat, entry.value)
        }
    }
}

context(GenerationEntity, GenerationContext)
internal fun TypeSpec.Builder.addSharedCompanionObjectContent() {
    addModifiers(PUBLIC)
    addProperty("entries", LIST.parameterizedBy(entityCN), PUBLIC) {
        addKdoc("A [List] of all known [%T]s.", entityCN)
        val optIns = entriesDistinctByValue
            .flatMap { it.additionalOptInMarkerAnnotations }
            .distinct()
            .map { name -> CodeBlock.of("%T::class", ClassName.bestGuess(name)) }
            .joinToCode()
        if (optIns.isNotEmpty()) {
            addAnnotation(OPT_IN) {
                addMember(optIns)
            }
        }
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
