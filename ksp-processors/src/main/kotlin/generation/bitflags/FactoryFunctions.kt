@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmName
import dev.kord.ksp.addFunction
import dev.kord.ksp.addParameter
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.shared.CONTRACT
import dev.kord.ksp.generation.shared.EXACTLY_ONCE
import dev.kord.ksp.generation.shared.GenerationContext
import dev.kord.ksp.withControlFlow

context(_: GenerationContext)
private val BitFlags.factoryFunctionName
    get() = collectionCN.simpleName

context(entity: BitFlags, context: GenerationContext)
internal fun FileSpec.Builder.addFactoryFunctions() {
    addFunction(entity.factoryFunctionName) {
        addKdoc("Returns an instance of [%T] built with [%T].", entity.collectionCN, entity.builderCN)
        addModifiers(PUBLIC, INLINE)
        addParameter("builder", type = LambdaTypeName.get(receiver = entity.builderCN, returnType = UNIT)) {
            defaultValue("{}")
        }
        returns(entity.collectionCN)
        addStatement("%M·{·callsInPlace(builder,·%M)·}", CONTRACT, EXACTLY_ONCE)
        addStatement("return·%T().apply(builder).build()", entity.builderCN)
    }
    addFactoryFunctionForIterable(baseParameterType = context.entityCN, IterableType.VARARG)
    addFactoryFunctionForIterable(baseParameterType = entity.collectionCN, IterableType.VARARG)
    addFactoryFunctionForIterable(baseParameterType = context.entityCN, IterableType.ITERABLE)
    addFactoryFunctionForIterable(baseParameterType = entity.collectionCN, IterableType.ITERABLE, jvmName = true)
}

private enum class IterableType { VARARG, ITERABLE }

context(entity: BitFlags, _:GenerationContext)
private fun FileSpec.Builder.addFactoryFunctionForIterable(
    baseParameterType: TypeName,
    iterableType: IterableType,
    jvmName: Boolean = false,
) = addFunction(entity.factoryFunctionName) {
    addKdoc(
        "Returns an instance of [%T] that has all bits set that are set in any element of [flags].",
        entity.collectionCN,
    )
    if (jvmName) jvmName(entity.factoryFunctionName + '0')
    addModifiers(PUBLIC)
    when (iterableType) {
        IterableType.VARARG -> addParameter("flags", baseParameterType, VARARG)
        IterableType.ITERABLE -> addParameter("flags", type = ITERABLE.parameterizedBy(baseParameterType))
    }
    returns(entity.collectionCN)
    withControlFlow("return ${entity.factoryFunctionName}") {
        addStatement("flags.forEach·{·+it·}")
    }
}
