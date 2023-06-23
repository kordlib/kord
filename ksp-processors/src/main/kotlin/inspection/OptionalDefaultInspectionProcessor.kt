package dev.kord.ksp.inspection

import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import dev.kord.ksp.getSymbolsWithAnnotation
import dev.kord.ksp.isClassifierReference
import kotlinx.serialization.Serializable

/** [SymbolProcessorProvider] for [OptionalDefaultInspectionProcessor]. */
class OptionalDefaultInspectionProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        OptionalDefaultInspectionProcessor(environment.logger)
}

private val OPTIONAL_TYPES =
    listOf("Optional", "OptionalBoolean", "OptionalInt", "OptionalLong", "OptionalSnowflake")
        .map { "dev.kord.common.entity.optional.$it" }
        .toSet()

/**
 * [SymbolProcessor] that verifies that every primary constructor parameter with `Optional` type of a [Serializable]
 * class has a default value.
 */
private class OptionalDefaultInspectionProcessor(private val logger: KSPLogger) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation<Serializable>()
            .filterIsInstance<KSClassDeclaration>()
            .forEach { it.verifySerializableClassPrimaryConstructor() }

        return emptyList() // we never have to defer any symbols
    }

    private fun KSClassDeclaration.verifySerializableClassPrimaryConstructor() {
        primaryConstructor?.parameters?.forEach { parameter ->
            if (parameter.hasDefault) return@forEach

            val type = parameter.type
            if (type.element?.isClassifierReference == false) return@forEach

            val clazz = when (val declaration = type.resolve().declaration) {
                is KSTypeParameter -> return@forEach
                is KSClassDeclaration -> declaration
                is KSTypeAlias -> declaration.findActualType()
                else -> error("Unexpected KSDeclaration: $declaration")
            }
            if (clazz.qualifiedName?.asString() in OPTIONAL_TYPES) {
                logger.error("Missing default for parameter ${parameter.name?.asString()}.", symbol = parameter)
            }
        }
    }
}
