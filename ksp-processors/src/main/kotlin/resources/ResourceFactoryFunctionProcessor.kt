package dev.kord.ksp.resources

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import dev.kord.ksp.*
import dev.kord.ksp.FileSpec
import dev.kord.ksp.addFunction
import dev.kord.ksp.getSymbolsWithAnnotation
import dev.kord.ksp.hasAnnotation
import io.ktor.resources.*

class ResourceFactoryFunctionProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        ResourceFactoryFunctionProcessor(environment.codeGenerator, environment.logger)
}

private class ResourceFactoryFunctionProcessor(private val generator: CodeGenerator, private val logger: KSPLogger) :
    SymbolProcessor {
    @OptIn(DelicateKotlinPoetApi::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation<Resource>()
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { declaration ->
                val file = declaration.containingFile ?: return@mapNotNull null
                val constructor = declaration.primaryConstructor
                    ?: return@mapNotNull null
                val receiver = (declaration.parentDeclaration as? KSClassDeclaration)?.toClassName()
                val parent = declaration.resourceParent ?: return@mapNotNull null
                if (parent != constructor.parameters.last().type.resolve()) {
                    logger.error("Parent is not last argument", declaration)
                }


                if (parent.declaration.parentDeclaration?.qualifiedName?.asString() == declaration.qualifiedName?.asString()) {
                    logger.error(
                        "Element references child as parent, this is likely because the parent has a child with the same name, please reference the parent explicitly like ${declaration.parentDeclaration(2)?.simpleName?.asString()}.${parent.declaration.simpleName.asString()}", declaration
                    )
                    // this would cause a stackoverflow in the kotlin compiler (since KSP runs in the daemon)
                    // and therefore the error would not get printed
                    return@mapNotNull null
                }
                val parameters = constructor.parameters
                    .filterNot { it.isParent() }
                    .toParameters()
                val parentParameters = parent.parentParameters
                    .reversed() // as we resolve parent parameters from bottom->top they're in reverse order
                FactoryFunction(
                    file,
                    declaration.toClassName(),
                    receiver,
                    parameters,
                    parentParameters,
                    parent.toClassName()
                )
            }
            .groupBy(FactoryFunction::file).forEach { (file, functions) ->
                val fileSpec = FileSpec("dev.kord.rest", "${file.fileName.dropLast(3)}Factories") {
                    addKotlinDefaultImports(includeJvm = false, includeJs = false)
                    addAnnotation(Suppress("NOTHING_TO_INLINE"))
                    functions.forEach {
                        with(it) {
                            addFunction()
                        }
                    }
                }

                fileSpec.writeTo(generator, false)
            }

        return emptyList()
    }

    private val KSClassDeclaration.resourceParent: KSType?
        get() = primaryConstructor?.let { constructor ->
            constructor.parameters
                .firstOrNull(KSValueParameter::isParent)
                ?.type?.resolve()
        }

    private val KSType.parameters: List<FactoryFunction.Parameter>
        get() = (declaration as KSClassDeclaration)
            .primaryConstructor!!.parameters
            .filterNot { it.isParent() } // we can assume that factory function exists for this parent as well
            .toParameters()

    @Suppress("RecursivePropertyAccessor")
    private val KSType.parentParameters: List<FactoryFunction.Parameter>
        get() = parameters + (declaration as KSClassDeclaration).resourceParent
            ?.parentParameters.orEmpty()

    private fun List<KSValueParameter>.toParameters(): List<FactoryFunction.Parameter> = map {
        FactoryFunction.Parameter(
            it.name?.asString() ?: error("Parameter missing name"),
            it.type.resolve().toClassName()
        )
    }
}

private fun KSValueParameter.isParent() = type.resolve().declaration.hasAnnotation<Resource>()

private data class FactoryFunction(
    val file: KSFile,
    val name: ClassName,
    val receiver: ClassName?,
    val parameters: List<Parameter>,
    val parentParameters: List<Parameter>,
    val parentName: ClassName
) {
    data class Parameter(val name: String, val type: ClassName) : ValueParameter {
        override fun toCodeBlock(): CodeBlock = CodeBlock.of("%N", name)
    }

    fun FileSpec.Builder.addFunction() {
        addFunction(this@FactoryFunction.name.simpleName) {
            returns(this@FactoryFunction.name)
            addOriginatingKSFile(file)
            addModifiers(KModifier.INLINE)
            if (receiver != null) {
                receiver(receiver.nestedClass("Companion"))
            }
            (parentParameters + this@FactoryFunction.parameters).forEach {
                addParameter(it.name, it.type)
            }

            val parentConstructorParameters = parentParameters.toValueParameterList()
            val parentConstruction = CodeBlock.of("%T(%L)", parentName, parentConstructorParameters)
            val constructorParameters =
                (this@FactoryFunction.parameters + Argument(parentConstruction)).toValueParameterList()

            addCode("return %T(%L)", this@FactoryFunction.name, constructorParameters)
        }
    }

    private fun List<ValueParameter>.toValueParameterList() =
        map(ValueParameter::toCodeBlock).joinToCode(", ")
}

private sealed interface ValueParameter {
    fun toCodeBlock(): CodeBlock
}

private data class Argument(private val codeBlock: CodeBlock) : ValueParameter {
    override fun toCodeBlock(): CodeBlock = codeBlock
}
