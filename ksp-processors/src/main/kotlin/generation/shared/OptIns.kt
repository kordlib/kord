package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.Annotatable
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.joinToCode
import dev.kord.codegen.kotlinpoet.addAnnotation

context(GenerationContext)
internal fun Annotatable.Builder<*>.addEntryOptIns() {
    val optIns = entriesDistinctByValue
        .flatMap { it.requiresOptInAnnotations }
        .distinct()
        .map { name -> CodeBlock.of("%T::class", ClassName.bestGuess(name)) }
        .joinToCode()
    if (optIns.isNotEmpty()) {
        addAnnotation(OPT_IN) {
            addMember(optIns)
        }
    }
}
