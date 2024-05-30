package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmName
import dev.kord.ksp.addAnnotation
import dev.kord.ksp.addFunction
import dev.kord.ksp.addParameter
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.shared.CONTRACT
import dev.kord.ksp.generation.shared.EXACTLY_ONCE
import dev.kord.ksp.generation.shared.GenerationContext
import dev.kord.ksp.withControlFlow

context(GenerationContext)
private val BitFlags.factoryFunctionName
    get() = collectionCN.simpleName

context(BitFlags, GenerationContext)
internal fun FileSpec.Builder.addFactoryFunctions() {
    addFunction(factoryFunctionName) {
        addKdoc("Returns an instance of [%T] built with [%T].", collectionCN, builderCN)
        addModifiers(PUBLIC, INLINE)
        addParameter("builder", type = LambdaTypeName.get(receiver = builderCN, returnType = UNIT)) {
            defaultValue("{}")
        }
        returns(collectionCN)
        addStatement("%M·{·callsInPlace(builder,·%M)·}", CONTRACT, EXACTLY_ONCE)
        addStatement("return·%T().apply(builder).build()", builderCN)
    }
    // TODO remove eventually
    if (hadBuilderFactoryFunction0) {
        @OptIn(DelicateKotlinPoetApi::class)
        addFunction(factoryFunctionName + '0') {
            addAnnotation(Suppress("FunctionName"))
            addAnnotation(Deprecated("Binary compatibility, keep for some releases.", level = DeprecationLevel.HIDDEN))
            addModifiers(PUBLIC, INLINE)
            addParameter("builder", type = LambdaTypeName.get(receiver = builderCN, returnType = UNIT)) {
                defaultValue("{}")
            }
            returns(collectionCN)
            addStatement("%M·{·callsInPlace(builder,·%M)·}", CONTRACT, EXACTLY_ONCE)
            addStatement("return·$factoryFunctionName(builder)", builderCN)
        }
    }
    addFactoryFunctionForIterable(baseParameterType = entityCN, IterableType.VARARG)
    addFactoryFunctionForIterable(baseParameterType = entityCN, IterableType.ITERABLE)
    addFactoryFunctionForIterable(baseParameterType = collectionCN, IterableType.ITERABLE, jvmName = true)
}

private enum class IterableType { VARARG, ITERABLE }

context(BitFlags, GenerationContext)
private fun FileSpec.Builder.addFactoryFunctionForIterable(
    baseParameterType: TypeName,
    iterableType: IterableType,
    jvmName: Boolean = false,
) = addFunction(factoryFunctionName) {
    addKdoc(
        "Returns an instance of [%T] that has all bits set that are set in any element of [flags].",
        collectionCN,
    )
    if (jvmName) jvmName(factoryFunctionName + '0')
    addModifiers(PUBLIC)
    when (iterableType) {
        IterableType.VARARG -> addParameter("flags", baseParameterType, VARARG)
        IterableType.ITERABLE -> addParameter("flags", type = ITERABLE.parameterizedBy(baseParameterType))
    }
    returns(collectionCN)
    withControlFlow("return $factoryFunctionName") {
        addStatement("flags.forEach·{·+it·}")
    }
}
