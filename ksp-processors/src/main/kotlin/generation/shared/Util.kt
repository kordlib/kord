package dev.kord.ksp.generation.shared

import com.squareup.kotlinpoet.Annotatable
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.joinToCode
import dev.kord.ksp.addAnnotation
import dev.kord.ksp.generation.GenerationEntity
import kotlin.DeprecationLevel.*

internal val GenerationEntity.Entry.nameWithSuppressedDeprecation
    get() = when (deprecated?.level) {
        null -> name
        WARNING -> """@Suppress("DEPRECATION")·$name"""
        ERROR, HIDDEN -> """@Suppress("DEPRECATION_ERROR")·$name"""
    }

context(GenerationContext)
internal fun Annotatable.Builder<*>.addEntryOptIns() {
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
}
