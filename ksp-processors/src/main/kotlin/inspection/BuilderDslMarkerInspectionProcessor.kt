package dev.kord.ksp.inspection

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import dev.kord.ksp.resolveAllNewClasses

class BuilderDslMarkerInspectionProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor
        = BuilderDslMarkerInspectionProcessor(environment.logger)
}

class BuilderDslMarkerInspectionProcessor(private val logger: KSPLogger) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.resolveAllNewClasses()
            .filter {
                it.superTypes.any { reference ->
                    val type = reference.resolve()
                    type.declaration.qualifiedName?.asString() == "dev.kord.rest.builder.RequestBuilder"
                }
            }
            .filter {
                it.annotations.none { annotation ->
                    annotation.annotationType.resolve()
                        .declaration.qualifiedName?.asString() == "dev.kord.common.annotation.KordDsl"
                }
            }
            .forEach {
                logger.error("Found builder without @KordDsl: ${it.qualifiedName?.asString()}")
            }

        return emptyList() // we never have to defer any symbols
    }
}
