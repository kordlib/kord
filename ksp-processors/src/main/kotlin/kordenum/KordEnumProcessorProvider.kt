package dev.kord.ksp.kordenum

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/** [SymbolProcessorProvider] for [KordEnumProcessor]. */
class KordEnumProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        KordEnumProcessor(environment.codeGenerator, environment.logger)
}
