package dev.kord.ksp.inspection

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.Modifier
import dev.kord.ksp.getNewClasses
import dev.kord.ksp.isOfType

class BuilderDslMarkerInspectionProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        BuilderDslMarkerInspectionProcessor(environment.logger)
}

private class BuilderDslMarkerInspectionProcessor(private val logger: KSPLogger) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getNewClasses()
            // some internal implementations are not annotated with @KordDsl on purpose
            .filterNot { Modifier.INTERNAL in it.modifiers }
            .filter {
                it.annotations.none { annotation ->
                    annotation.isOfType("dev.kord.common.annotation", "KordDsl")
                }
            }
            .filter {
                it.getAllSuperTypes().any { type ->
                    type.declaration.qualifiedName?.asString() == "dev.kord.rest.builder.RequestBuilder"
                }
            }
            .forEach {
                logger.error("Found builder without @KordDsl", symbol = it)
            }

        return emptyList() // we never have to defer any symbols
    }
}
