package dev.kord.ksp.generation

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ksp.writeTo
import dev.kord.ksp.Generate
import dev.kord.ksp.generation.bitflags.generateFileSpec
import dev.kord.ksp.generation.kordenum.generateFileSpec
import dev.kord.ksp.getAnnotationsByType
import dev.kord.ksp.getSymbolsWithAnnotation
import dev.kord.ksp.isOfType

/** [SymbolProcessorProvider] for [GenerationProcessor]. */
class GenerationProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        GenerationProcessor(environment.codeGenerator, environment.logger)
}

/** [SymbolProcessor] for [Generate] annotation. */
class GenerationProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {

    override fun finish() {
        logger.info("GenerationProcessor received finish signal")
    }

    override fun onError() {
        logger.info("GenerationProcessor received error signal")
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("GenerationProcessor got called, resolving annotations...")

        resolver
            .getSymbolsWithAnnotation<Generate>()
            .onEach { if (it !is KSFile) logger.warn("found annotation on wrong symbol", symbol = it) }
            .filterIsInstance<KSFile>()
            .forEach(::processFile)

        logger.info("GenerationProcessor finished processing annotations")

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
        logger.info("generating ${entity.entityName}...")

        val fileSpec = when (entity) {
            is GenerationEntity.BitFlags -> entity.generateFileSpec(originatingFile)
            is GenerationEntity.KordEnum -> entity.generateFileSpec(originatingFile)
        }

        // this output is isolating, see https://kotlinlang.org/docs/ksp-incremental.html#aggregating-vs-isolating
        fileSpec.writeTo(codeGenerator, aggregating = false)

        logger.info("finished generating ${entity.entityName}")
    }
}
