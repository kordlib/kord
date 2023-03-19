package dev.kord.ksp.graal

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import dev.kord.ksp.getSymbolsWithAnnotation
import kotlinx.serialization.Serializable

class KordGraalProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KordGraalProcessor(
            environment.codeGenerator, environment.logger
        )
    }
}

private val entries = mutableListOf<ReflectConfigEntry>()

private class KordGraalProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) :
    SymbolProcessor {
    override fun finish() {
        flushEntries()
        logger.info("KordGraalProcessor received finish signal")
    }

    override fun onError() {
        logger.info("KordGraalProcessor received error signal")
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("KordGraalProcessor got called, resolving annotations...")

        entries += resolver
            .getSymbolsWithAnnotation<Serializable>()
            .filterIsInstance<KSClassDeclaration>()
            .flatMap(::processClass)
            .toList()

        logger.info("KordEnumProcessor finished processing annotations")

        return emptyList() // we never have to defer any symbols
    }

    private fun flushEntries() {
        if (entries.isNotEmpty()) {
            val config = ReflectConfig(entries)
            val file = codeGenerator
                .createNewFile(Dependencies.ALL_FILES, "META-INF.native-image", "reflect-config", "json")
            file.bufferedWriter().use { it.write(config.encode()) }
        }
    }

    private fun processClass(clazz: KSClassDeclaration): List<ReflectConfigEntry> {
        val qualifiedName = clazz.qualifiedName?.asString() ?: run {
            logger.warn("Invalid Serializable element received", clazz)
            return emptyList()
        }
        val companionField = ReflectConfigEntry(
            name = qualifiedName,
            fields = listOf(ReflectConfigEntry.Field("Companion"))
        )
        val companionObject = ReflectConfigEntry(
            name = "$qualifiedName\$Companion",
            methods = listOf(ReflectConfigEntry.Method("serializer"))
        )

        return listOf(companionField, companionObject)
    }
}
