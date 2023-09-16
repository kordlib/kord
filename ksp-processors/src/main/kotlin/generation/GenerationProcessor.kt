package dev.kord.ksp.generation

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ksp.writeTo
import dev.kord.codegen.ksp.getSymbolsWithAnnotation
import dev.kord.codegen.ksp.isOfType
import dev.kord.ksp.Generate
import dev.kord.ksp.generation.bitflags.generateFileSpec
import dev.kord.ksp.generation.kordenum.generateFileSpec
import dev.kord.ksp.getAnnotationsByType

/** [SymbolProcessorProvider] for [GenerationProcessor]. */
class GenerationProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        GenerationProcessor(environment.codeGenerator, environment.logger)
}

/** [SymbolProcessor] that generates files for [Generate] annotations. */
private class GenerationProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver
            .getSymbolsWithAnnotation<Generate>()
            .onEach { if (it !is KSFile) logger.warn("found annotation on wrong symbol", symbol = it) }
            .filterIsInstance<KSFile>()
            .forEach(::processFile)

        return emptyList() // we never have to defer any symbols
    }

    private fun processFile(file: KSFile) {
        val generates = file.getAnnotationsByType<Generate>()
        val annotations = file.annotations.filter { it.isOfType<Generate>() }
        (generates zip annotations)
            .mapNotNull { (generate, annotation) -> generate.toGenerationEntityOrNull(logger, annotation) }
            .forEach { generateEntity(it, originatingFile = file) }
    }

    private fun generateEntity(entity: GenerationEntity, originatingFile: KSFile) {
        val fileSpec = when (entity) {
            is GenerationEntity.BitFlags -> entity.generateFileSpec(originatingFile)
            is GenerationEntity.KordEnum -> entity.generateFileSpec(originatingFile)
        }

        // this output is isolating, see https://kotlinlang.org/docs/ksp-incremental.html#aggregating-vs-isolating
        fileSpec.writeTo(codeGenerator, aggregating = false)
    }
}
