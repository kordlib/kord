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

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addContains(parameterName: String, parameterType: TypeName) = addFunction("contains") {
    addKdoc("Checks if this instance of [%T] has all bits set that are set in [$parameterName].", collectionCN)
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns<Boolean>()
    addStatement(
        when (valueType) {
            INT -> "return this.$valueName·and·$parameterName.$valueName·==·$parameterName.$valueName"
            BIT_SET -> "return $parameterName.$valueName·in·this.$valueName"
        }
    )
}

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addPlus(parameterName: String, parameterType: TypeName) = addFunction("plus") {
    addKdoc(
        "Returns an instance of [%T] that has all bits set that are set in `this` and [$parameterName].",
        collectionCN,
    )
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns(collectionCN)
    addStatement(
        when (valueType) {
            INT -> "return %T(this.$valueName·or·$parameterName.$valueName)"
            BIT_SET -> "return %T(this.$valueName·+·$parameterName.$valueName)"
        },
        collectionCN,
    )
}

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addMinus(parameterName: String, parameterType: TypeName) = addFunction("minus") {
    addKdoc(
        "Returns an instance of [%T] that has all bits set that are set in `this` except the bits that are set in " +
            "[$parameterName].",
        collectionCN,
    )
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns(collectionCN)
    addStatement(
        when (valueType) {
            INT -> "return %T(this.$valueName·and·$parameterName.$valueName.inv())"
            BIT_SET -> "return %T(this.$valueName·-·$parameterName.$valueName)"
        },
        collectionCN,
    )
}

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addCopy() = addFunction("copy") {
    addKdoc("Returns a copy of this instance of [%T] modified with [builder].", collectionCN)
    addModifiers(PUBLIC, INLINE)
    addParameter("builder", type = LambdaTypeName.get(receiver = builderCN, returnType = UNIT))
    returns(collectionCN)
    addStatement("%M·{·callsInPlace(builder,·%M)·}", CONTRACT, EXACTLY_ONCE)
    val valueCopy = when (valueType) {
        INT -> ""
        BIT_SET -> ".copy()"
    }
    addStatement("return·%T($valueName$valueCopy).apply(builder).build()", builderCN)
}

// TODO remove eventually
context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addCopy0() = addFunction("copy0") {
    @OptIn(DelicateKotlinPoetApi::class)
    addAnnotation(
        Deprecated(
            "Kept for binary compatibility, this declaration will be removed in 0.17.0.",
            level = DeprecationLevel.HIDDEN,
        )
    )
    addModifiers(PUBLIC, INLINE)
    addParameter("builder", type = LambdaTypeName.get(receiver = builderCN, returnType = UNIT))
    returns(collectionCN)
    addStatement("%M·{·callsInPlace(builder,·%M)·}", CONTRACT, EXACTLY_ONCE)
    addStatement("return·copy(builder)")
}
