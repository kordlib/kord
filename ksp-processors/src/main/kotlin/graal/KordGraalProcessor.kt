package dev.kord.ksp.graal

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import dev.kord.ksp.GraalVisible
import dev.kord.ksp.getSymbolsWithAnnotation
import dev.kord.ksp.jvmBinaryName
import kotlinx.serialization.KSerializer
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

        entries += resolver
            .getSymbolsWithAnnotation<GraalVisible>()
            .filterIsInstance<KSClassDeclaration>()
            .map { ReflectConfigEntry(name = it.jvmBinaryName) }

        logger.info("KordEnumProcessor finished processing annotations")

        return emptyList() // we never have to defer any symbols
    }

    private fun flushEntries() {
        if (entries.isNotEmpty()) {
            val config = ReflectConfig(entries.distinctBy(ReflectConfigEntry::name))
            val file = codeGenerator
                .createNewFile(Dependencies.ALL_FILES, "META-INF.native-image", "reflect-config", "json")
            file.bufferedWriter().use { it.write(config.encode()) }
        }
    }

    private fun processClass(clazz: KSClassDeclaration): List<ReflectConfigEntry> {
        val name = clazz.findCompanionObjectName()

        val qualifiedName = clazz.jvmBinaryName
        val companionField = ReflectConfigEntry(
            name = qualifiedName,
            fields = listOf(ReflectConfigEntry.Field(name))
        )
        val companionObject = ReflectConfigEntry(
            name = "$qualifiedName\$$name",
            methods = listOf(
                ReflectConfigEntry.Method(
                    "serializer",
                    generateTypeParameters(clazz.typeParameters.count()).also {
                        logger.info("Class ${clazz.qualifiedName?.asString()} has ${it.size} type parameters")
                    }
                )
            )
        )

        return listOf(companionField, companionObject)
    }

    private fun KSClassDeclaration.findCompanionObjectName(): String {
        val companionObject = declarations.firstOrNull {
            (it as? KSClassDeclaration)?.isCompanionObject == true
        }

        return companionObject?.qualifiedName?.getShortName() ?: "Companion"
    }

    private fun generateTypeParameters(n: Int): List<String> = List(n) { KSerializer::class.qualifiedName!! }

}
