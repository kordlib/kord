@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import dev.kord.ksp.addAnnotation
import dev.kord.ksp.addFunction
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.CONTRACT
import dev.kord.ksp.generation.shared.EXACTLY_ONCE
import dev.kord.ksp.generation.shared.GenerationContext
import dev.kord.ksp.returns

context(entity: BitFlags, _: GenerationContext)
internal fun TypeSpec.Builder.addContains(parameterName: String, parameterType: TypeName) = addFunction("contains") {
    addKdoc("Checks if this instance of [%T] has all bits set that are set in [$parameterName].", entity.collectionCN)
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns<Boolean>()
    addStatement(
        when (entity.valueType) {
            INT -> "return this.${entity.valueName}·and·$parameterName.${entity.valueName}·==·$parameterName.${entity.valueName}"
            BIT_SET -> "return $parameterName.${entity.valueName}·in·this.${entity.valueName}"
        }
    )
}

context(entity: BitFlags, _: GenerationContext)
internal fun TypeSpec.Builder.addPlus(parameterName: String, parameterType: TypeName) = addFunction("plus") {
    addKdoc(
        "Returns an instance of [%T] that has all bits set that are set in `this` and [$parameterName].",
        entity.collectionCN,
    )
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns(entity.collectionCN)
    addStatement(
        when (entity.valueType) {
            INT -> "return %T(this.${entity.valueName}·or·$parameterName.${entity.valueName})"
            BIT_SET -> "return %T(this.${entity.valueName}·+·$parameterName.${entity.valueName})"
        },
        entity.collectionCN,
    )
}

context(entity: BitFlags, _: GenerationContext)
internal fun TypeSpec.Builder.addMinus(parameterName: String, parameterType: TypeName) = addFunction("minus") {
    addKdoc(
        "Returns an instance of [%T] that has all bits set that are set in `this` except the bits that are set in " +
            "[$parameterName].",
        entity.collectionCN,
    )
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns(entity.collectionCN)
    addStatement(
        when (entity.valueType) {
            INT -> "return %T(this.${entity.valueName}·and·$parameterName.${entity.valueName}.inv())"
            BIT_SET -> "return %T(this.${entity.valueName}·-·$parameterName.${entity.valueName})"
        },
        entity.collectionCN,
    )
}

context(entity: BitFlags, _: GenerationContext)
internal fun TypeSpec.Builder.addCopy() = addFunction("copy") {
    addKdoc("Returns a copy of this instance of [%T] modified with [builder].", entity.collectionCN)
    addModifiers(PUBLIC, INLINE)
    addParameter("builder", type = LambdaTypeName.get(receiver = entity.builderCN, returnType = UNIT))
    returns(entity.collectionCN)
    addStatement("%M·{·callsInPlace(builder,·%M)·}", CONTRACT, EXACTLY_ONCE)
    val valueCopy = when (entity.valueType) {
        INT -> ""
        BIT_SET -> ".copy()"
    }
    addStatement("return·%T(${entity.valueName}$valueCopy).apply(builder).build()", entity.builderCN)
}

// TODO remove eventually
context(entity: BitFlags, _: GenerationContext)
internal fun TypeSpec.Builder.addCopy0() = addFunction("copy0") {
    @OptIn(DelicateKotlinPoetApi::class)
    addAnnotation(
        Deprecated(
            "Kept for binary compatibility, this declaration will be removed in 0.17.0.",
            level = DeprecationLevel.HIDDEN,
        )
    )
    addModifiers(PUBLIC, INLINE)
    addParameter("builder", type = LambdaTypeName.get(receiver = entity.builderCN, returnType = UNIT))
    returns(entity.collectionCN)
    addStatement("%M·{·callsInPlace(builder,·%M)·}", CONTRACT, EXACTLY_ONCE)
    addStatement("return·copy(builder)")
}
