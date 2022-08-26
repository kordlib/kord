package dev.kord.ksp.kordenum

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ksp.writeTo
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.getSymbolsWithAnnotation
import dev.kord.ksp.isOfType

/** [SymbolProcessor] for [GenerateKordEnum] annotation. */
class KordEnumProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {

    override fun finish() {
        logger.info("KordEnumProcessor received finish signal")
    }

    override fun onError() {
        logger.info("KordEnumProcessor received error signal")
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("KordEnumProcessor got called, resolving annotations...")

        resolver
            .getSymbolsWithAnnotation<GenerateKordEnum>()
            .onEach { if (it !is KSFile) logger.warn("found annotation on wrong symbol", symbol = it) }
            .filterIsInstance<KSFile>()
            .forEach(::processFile)

        logger.info("KordEnumProcessor finished processing annotations")

        return emptyList() // we never have to defer any symbols
    }

    private fun processFile(file: KSFile) {
        file.annotations
            .filter { it.isOfType<GenerateKordEnum>() }
            .onEach { logger.info("found annotation", symbol = it) }
            .mapNotNull { it.toKordEnumOrNull(logger) }
            .forEach { generateKordEnum(it, originatingFile = file) }
    }

    private fun generateKordEnum(kordEnum: KordEnum, originatingFile: KSFile) {
        logger.info("generating ${kordEnum.name}...")

        val kordEnumFileSpec = kordEnum.generateFileSpec(originatingFile)

        // this output is isolating, see https://kotlinlang.org/docs/ksp-incremental.html#aggregating-vs-isolating
        kordEnumFileSpec.writeTo(codeGenerator, aggregating = false)

        logger.info("finished generating ${kordEnum.name}")
    }
}
