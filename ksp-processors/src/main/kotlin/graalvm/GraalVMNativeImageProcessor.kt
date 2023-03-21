package dev.kord.ksp.graalvm

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import dev.kord.ksp.AvailableForReflectionOnGraalVMNativeImage
import dev.kord.ksp.getSymbolsWithAnnotation
import dev.kord.ksp.jvmBinaryName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

/** [SymbolProcessorProvider] for [GraalVMNativeImageProcessor]. */
class GraalVMNativeImageProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        GraalVMNativeImageProcessor(environment.codeGenerator, environment.logger, environment.options["project"]!!)
}

/**
 * [SymbolProcessor] that reads [Serializable] and [AvailableForReflectionOnGraalVMNativeImage] annotations and
 * generates [`reflect-config.json`](https://www.graalvm.org/latest/reference-manual/native-image/metadata/#reflection)
 * files for building native executables with
 * [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/).
 */
private class GraalVMNativeImageProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val project: String,
) : SymbolProcessor {

    private var flushedEntries = false
    private val entries = mutableListOf<ReflectConfigEntry>()

    override fun finish() {
        logger.info("GraalVMNativeImageProcessor received finish signal")
        flushEntries()
    }

    override fun onError() {
        val count = entries.size
        if (flushedEntries) {
            logger.info("GraalVMNativeImageProcessor received error signal after $count entries were flushed")
        } else {
            logger.warn("GraalVMNativeImageProcessor received error signal while having $count unflushed entries")
        }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("GraalVMNativeImageProcessor got called, resolving annotations...")

        entries += resolver
            .getSymbolsWithAnnotation<Serializable>()
            .filterIsInstance<KSClassDeclaration>()
            .flatMap(::processClass)
            .toList()

        entries += resolver
            .getSymbolsWithAnnotation<AvailableForReflectionOnGraalVMNativeImage>()
            .onEach { if (it !is KSClassDeclaration) logger.warn("found annotation on wrong symbol", symbol = it) }
            .filterIsInstance<KSClassDeclaration>()
            .map { ReflectConfigEntry(name = it.jvmBinaryName) }

        logger.info("GraalVMNativeImageProcessor finished processing annotations")

        return emptyList() // we never have to defer any symbols
    }

    private fun flushEntries() {
        check(!flushedEntries) { "already flushed entries" }
        flushedEntries = true
        if (entries.isNotEmpty()) {
            logger.info("flushing entries for project $project...")
            val file = codeGenerator
                .createNewFileByPath(
                    Dependencies.ALL_FILES,
                    "META-INF/native-image/dev.kord/kord-${project}/reflect-config",
                    "json"
                )
            file.bufferedWriter().use { it.write(entries.encodeToJson()) }
            logger.info("finished flushing entries for project $project...")
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
